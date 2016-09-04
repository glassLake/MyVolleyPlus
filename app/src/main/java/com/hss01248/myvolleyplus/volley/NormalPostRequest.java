package com.hss01248.myvolleyplus.volley;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by hss01248 on 9/14/2015.
 */
public class NormalPostRequest extends Request<JSONObject> {


    private static final int SOCKET_TIMEOUT =20000 ;
    private Map  mMap;
    private Response.Listener<JSONObject> mListener;

    public NormalPostRequest(int method,String url, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener, Map map) {
        super(method,url,errorListener);//method设置为1-->从下面的构造函数传入

        mListener = listener;
        mMap = map;
    }

    /**
     * 供使用者调用的构造方法
     * @param url
     * @param listener
     * @param errorListener
     * @param map
     */
    public NormalPostRequest(String url, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener, Map map) {
       this(Method.POST,url,listener,errorListener,map);

    }

    public NormalPostRequest(String url, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener, Map map,boolean isGet) {
            this(Method.GET,url,listener,errorListener,map);

    }


    public NormalPostRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }



    //mMap是已经按照前面的方式,设置了参数的实例
    @Override
    protected Map getParams() throws AuthFailureError {
        if (getMethod() == Method.GET ){
            return null;
        }
        return mMap;
    }

    //此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
          // Logger.e(jsonString);
           // FileUtils.saveAsFileInSDCard(jsonString, "thirdLoginResonse" + ".txt");
            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
    @Override
    protected void deliverResponse(JSONObject response) {
       // Logger.json(response.toString());
        if (isCanceled()){
            return ;
        }
        mListener.onResponse(response);
    }

    @Override
    public RetryPolicy getRetryPolicy()
    {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

}
