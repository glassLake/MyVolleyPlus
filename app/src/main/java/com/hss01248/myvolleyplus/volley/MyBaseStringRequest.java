package com.hss01248.myvolleyplus.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/4.
 */
public class MyBaseStringRequest extends Request<String> {
    protected Map<String,String> mMap;

    private Response.Listener<String> mListener;


    public MyBaseStringRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    public MyBaseStringRequest(int method, String url, Priority priority, Response.Listener<String> successListener,
                               Response.ErrorListener listener, RetryPolicy retryPolicy, Map map) {
        super(method, url, priority, listener, retryPolicy);

        mListener = successListener;
        mMap = map;
        setParams(mMap);

    }






    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        Log.e("data","parseNetworkResponse:"+parsed);
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }


    /**
     * 缓存key的生成规则:url+body
     * @return
     */
    @Override
    public String getCacheKey() {
        String bodyStr = "";
        try {

            byte[]   body = getBody();
            bodyStr = new String(body,"UTF-8");
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (bodyStr != null){
            return super.getCacheKey()+bodyStr;
        }else {
            return super.getCacheKey();
        }


    }


    @Override
    public void setParams(Map<String, String> params) {
        super.setParams(params);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    @Override
    protected void deliverResponse(String response) {
        Log.e("deliverResponse","deliverResponse:"+response);
        if (mListener != null)
        mListener.onResponse(response);
    }
}
