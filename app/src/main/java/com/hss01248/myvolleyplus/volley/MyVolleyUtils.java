package com.hss01248.myvolleyplus.volley;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.VolleyError;
import com.android.volley.request.DownloadRequest;
import com.android.volley.request.MultiPartRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.hss01248.myvolleyplus.config.ConfigInfo;
import com.hss01248.myvolleyplus.config.NetBaseBean;
import com.hss01248.myvolleyplus.config.NetConfig;
import com.hss01248.myvolleyplus.wrapper.MyNetCallback;
import com.hss01248.myvolleyplus.wrapper.MyNetUtil;
import com.hss01248.myvolleyplus.wrapper.TimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/15 0015.
 */
public class MyVolleyUtils {
    private static final long MIN_NET_TIME = NetConfig.TIME_MINI;//访问网络到回调至少要1500ms,不足的话也要补足.主要是用于弹出dialog的效果
    static RequestQueue requestQueue ;
    private static MyVolleyUtils instance;

    private MyVolleyUtils(Context context){
        requestQueue =  Volley.newRequestQueue(context);
    }

    public static  MyVolleyUtils getInstance(Context context){
        if (instance == null){
            synchronized (MyVolleyUtils.class){
                if (instance ==  null){
                    instance = new MyVolleyUtils(context);
                }
            }
        }
        return  instance;
    }




    /**
     * 基礎方法,可以從response中拿到string形式的body
     * @param method
     * @param urlTail
     * @param map
     * @param configInfo
     * @param myListener
     */
    public void sendRequest(final int method, final String urlTail, final Map map, final ConfigInfo configInfo,
                            final MyNetCallback myListener){
        String url = appendUrl(urlTail);

        addToken(map);



        RetryPolicy retryPolicy = generateRetryPolicy(configInfo);
        Request.Priority priority = getPriority(configInfo.priority);

        Request request = generateNewRequest(method,url,priority,map,configInfo,retryPolicy,myListener);

        setInfoToRequest(configInfo,request);

        //cachecontrol


        requestQueue.add(request);
    }

    private Request generateNewRequest(int method, String url, Request.Priority priority, Map map,
                                       ConfigInfo configInfo, RetryPolicy retryPolicy, MyNetCallback myListener) {
        int requestType = configInfo.resonseType;
        switch (requestType){
            case ConfigInfo.TYPE_STRING:
            case ConfigInfo.TYPE_JSON:
            case ConfigInfo.TYPE_JSON_FORMATTED:
                return newStringRequest(method,url,priority,map,configInfo,retryPolicy,myListener);
            case ConfigInfo.TYPE_DOWNLOAD:
                return newDownloadRequest(method,url,priority,map,configInfo,retryPolicy,myListener);
            case ConfigInfo.TYPE_UPLOAD:
                return newSingleUploadRequest(method,url,priority,map,configInfo,retryPolicy,myListener);
        }
        return null;
    }


    /**
     * todo
     * @param method
     * @param url
     * @param priority
     * @param map
     * @param configInfo
     * @param retryPolicy
     * @param myListener
     * @return
     */
    private Request newSingleUploadRequest(int method, String url, Request.Priority priority, Map map, ConfigInfo configInfo, RetryPolicy retryPolicy, final MyNetCallback myListener) {
        MultiPartRequest request = new SimpleMultiPartRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myListener.onSuccess(response,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myListener.onError(error.toString());
            }
        });
        request.setOnProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                myListener.onProgressChange(totalSize,transferredBytes);
            }
        });

        //request.addMultipartParam()
        return request;
    }

    private Request newDownloadRequest(int method, String url, Request.Priority priority, Map map,
                                       final ConfigInfo configInfo, RetryPolicy retryPolicy, final MyNetCallback myListener) {
        //todo 文件保存路徑的生成...

        DownloadRequest request =
         new DownloadRequest(url, configInfo.filePath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {//返回的是文件保存路徑
                myListener.onSuccess(response,configInfo.filePath);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myListener.onError(error.toString());
            }
        });

        request.setOnProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                myListener.onProgressChange(totalSize,transferredBytes);
            }
        });

        return request;
    }

    private Request newStringRequest(final int method, final String url, Request.Priority priority,
                                     final Map map, final ConfigInfo configInfo, RetryPolicy retryPolicy, final MyNetCallback myListener) {
        final long time = System.currentTimeMillis();
        return new MyBaseStringRequest(method,url, priority,new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {


                long time2 = System.currentTimeMillis();
                long gap = time2 - time;

                if (configInfo.isForceMinTime && (gap < MIN_NET_TIME)){
                    TimerUtil.doAfter(new TimerTask() {
                        @Override
                        public void run() {
                            parseStringResponse(response,method,url,map,configInfo,myListener);
                        }
                    },(MIN_NET_TIME - gap));

                }else {
                    parseStringResponse(response,method,url,map,configInfo,myListener);
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {

                long time2 = System.currentTimeMillis();

                long gap = time2 - time;

                if (configInfo.isForceMinTime && (gap < MIN_NET_TIME)){
                    SystemClock.sleep(MIN_NET_TIME - gap);
                    myListener.onError(error.toString());
                }else {
                    myListener.onError(error.toString());
                }



            }
        },retryPolicy,map);
    }

    private void setInfoToRequest(ConfigInfo configInfo,Request request) {
        request.setTag(configInfo.tag);
        request.setShouldCache(configInfo.shouldCache);
        request.setCacheTime(configInfo.cacheTime);
        request.setForceGetNet(configInfo.forceGetNet);
    }

    private RetryPolicy generateRetryPolicy(ConfigInfo configInfo) {
        return new DefaultRetryPolicy(configInfo.timeout,configInfo.retryCount,1.0f);
    }

    private void addToken(Map map) {
        if (map != null)
        map.put(NetConfig.TOKEN, NetConfig.getToken());//每一个请求都传递sessionid
    }

    private String appendUrl(String urlTail) {
        String url ;
        if (urlTail.contains("http:")|| urlTail.contains("https:")){
            url =  urlTail;
        }else {
            url = NetConfig.baseUrl+  urlTail;
        }

        return url;
    }

    private void parseStringResponse(String response, int method, String urlTail, Map map, ConfigInfo configInfo, MyNetCallback myListener) {

        switch (configInfo.resonseType){
            case ConfigInfo.TYPE_STRING:
                myListener.onSuccess(response,response);
                break;
            case ConfigInfo.TYPE_JSON:{
                JSONObject jsonObject = null;
                try {
                     jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    myListener.onError("resonse is not a Json");
                    break;
                }
                myListener.onSuccess(jsonObject,response);}
                break;

            case ConfigInfo.TYPE_JSON_FORMATTED:
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    myListener.onError("resonse is not a Json");
                    break;
                }
                String data = jsonObject.optString(NetConfig.KEY_DATA);
                String codeStr = jsonObject.optString(NetConfig.KEY_CODE);
                String msg = jsonObject.optString(NetConfig.KEY_MSG);
                int code = NetBaseBean.CODE_NONE;
                if (TextUtils.isEmpty(codeStr) ){
                    try {
                        code = Integer.parseInt(codeStr);
                    }catch (Exception e){

                    }
                }

                parseStandardJsonResponse(jsonObject,response,data,code,msg, method,  urlTail,  map,  configInfo,  myListener);

               // myListener.onSuccess(jsonObject,response,data,code,msg);
                break;
        }

    }

    private void parseStandardJsonResponse(JSONObject jsonObject, String response, String data, int code,
                                           String msg, final int method, final String urlTail, final Map map,
                                           final ConfigInfo configInfo, final MyNetCallback myListener) {

        switch (code){
            case NetBaseBean.CODE_SUCCESS://请求成功

                if (TextUtils.isEmpty(data) || "[]".equals(data) || "{}".equals(data)) {
                    myListener.onEmpty();

                } else {
                    myListener.onSuccess(response, data);
                }
                break;
            case NetBaseBean.CODE_UN_FOUND://系统错误
                myListener.onUnFound();
                break;
            case NetBaseBean.CODE_UNLOGIN://未登录
                MyNetUtil.autoLogin(new MyNetCallback() {
                    @Override
                    public void onSuccess(Object response, String resonseStr) {
                        sendRequest(method,  urlTail,  map,  configInfo,  myListener);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        myListener.onUnlogin();
                    }
                });
                break;

            default:
                myListener.onError(response.toString());
                break;
        }

    }

    private Request.Priority getPriority(int priority) {
        switch (priority){
            case ConfigInfo.Priority_NORMAL:
                return Request.Priority.NORMAL;
            case ConfigInfo.Priority_IMMEDIATE:
                return Request.Priority.IMMEDIATE;
            case ConfigInfo.Priority_LOW:
                return Request.Priority.LOW;
            case ConfigInfo.Priority_HIGH:
                return Request.Priority.HIGH;

        }

        return Request.Priority.NORMAL;

    }


    public  void cancelRequest(Object tag){
       requestQueue.cancelAll(tag);
    }










    public void getString(String url,Map map, String tag, final MyNetCallback listener){

        ConfigInfo info = new ConfigInfo();
        info.tag = tag;

        sendRequest(Request.Method.GET,url,map,info,listener);
    }


    public void postJsonRequest(String url,Map map, String tag, final MyNetCallback listener){

        ConfigInfo info = new ConfigInfo();
        info.tag = tag;
        info.resonseType = ConfigInfo.TYPE_JSON_FORMATTED;

        sendRequest(Request.Method.POST,url,map,info,listener);
    }



    public void download(String url,String savedpath,MyNetCallback<String> callback){
        ConfigInfo info = new ConfigInfo();
        info.tag = url;
        info.resonseType = ConfigInfo.TYPE_DOWNLOAD;
        info.filePath = savedpath;
        sendRequest(Request.Method.POST,url,null,info,callback);

    }

















}
