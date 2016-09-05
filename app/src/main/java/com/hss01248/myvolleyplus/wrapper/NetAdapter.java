package com.hss01248.myvolleyplus.wrapper;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.hss01248.myvolleyplus.config.ConfigInfo;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public abstract class NetAdapter<T> {

    public  Context context;

    public void initInApp(Context context){
        this.context = context;

    }







    public void sendRequest(final int method, final String urlTail, final Map map, final ConfigInfo configInfo,
                            final MyNetCallback myListener){
        String url = CommonHelper.appendUrl(urlTail);

        myListener.url = url;

        CommonHelper.addToken(map);

        T request = generateNewRequest(method,url,map,configInfo,myListener);

        setInfoToRequest(configInfo,request);

        //cachecontrol

        cacheControl(configInfo,request);


        addToQunue(request);
    }

    protected abstract void addToQunue(T request);

    protected abstract void cacheControl(ConfigInfo configInfo, T request);

    protected T generateNewRequest(int method, String url, Map map,
                                       ConfigInfo configInfo, MyNetCallback myListener) {
        int requestType = configInfo.resonseType;
        switch (requestType){
            case ConfigInfo.TYPE_STRING:
            case ConfigInfo.TYPE_JSON:
            case ConfigInfo.TYPE_JSON_FORMATTED:
                return newStringRequest(method,url,map,configInfo,myListener);
            case ConfigInfo.TYPE_DOWNLOAD:
                return newDownloadRequest(method,url,map,configInfo,myListener);
            case ConfigInfo.TYPE_UPLOAD:
                return newSingleUploadRequest(method,url,map,configInfo,myListener);
        }
        return null;
    }

    protected abstract T newSingleUploadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);

    protected abstract T newDownloadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);

    protected abstract T newStringRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener);




    protected abstract void setInfoToRequest(ConfigInfo configInfo,T request);

    protected RetryPolicy generateRetryPolicy(ConfigInfo configInfo) {
        return new DefaultRetryPolicy(configInfo.timeout,configInfo.retryCount,1.0f);
    }







    protected Request.Priority getPriority(int priority) {
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


    public abstract void cancelRequest(Object tag);









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
