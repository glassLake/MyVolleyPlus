package com.hss01248.myvolleyplus.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.DownloadRequest;
import com.android.volley.toolbox.Volley;
import com.hss01248.myvolleyplus.config.ConfigInfo;
import com.hss01248.myvolleyplus.wrapper.CommonHelper;
import com.hss01248.myvolleyplus.wrapper.MyNetCallback;
import com.hss01248.myvolleyplus.wrapper.NetAdapter;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class NewVolleyUtil extends NetAdapter<Request> {
    static RequestQueue requestQueue ;
    private static NewVolleyUtil instance;

    private NewVolleyUtil(Context context){
        requestQueue =  Volley.newRequestQueue(context);
    }

    public static  NewVolleyUtil getInstance(Context context){
        if (instance == null){
            synchronized (MyVolleyUtils.class){
                if (instance ==  null){
                    instance = new NewVolleyUtil(context);
                }
            }
        }
        return  instance;
    }



    @Override
    protected void addToQunue(Request request) {
        requestQueue.add(request);

    }

    @Override
    protected void cacheControl(ConfigInfo configInfo, Request request) {
        request.setShouldCache(configInfo.shouldCache);
        request.setCacheTime(configInfo.cacheTime);
        request.setForceGetNet(configInfo.forceGetNet);
    }

    @Override
    protected Request newSingleUploadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener) {
        return null;
    }

    @Override
    protected Request newDownloadRequest(int method, String url, Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {
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

    @Override
    protected Request newStringRequest(final int method, final String url, final Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {
        final long time = System.currentTimeMillis();
        return new MyBaseStringRequest(method,url, getPriority(configInfo.priority),new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {


                CommonHelper.parseStringResponseInTime(time,response,method,url,map,configInfo,myListener);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {

                CommonHelper.parseErrorInTime(time,error.toString(),configInfo,myListener);

            }
        },generateRetryPolicy(configInfo),map);
    }

    @Override
    protected void setInfoToRequest(ConfigInfo configInfo, Request request) {
        request.setTag(configInfo.tag);
    }

    @Override
    public void cancelRequest(Object tag) {
        requestQueue.cancelAll(tag);
    }
}
