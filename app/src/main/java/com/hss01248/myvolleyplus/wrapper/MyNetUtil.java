package com.hss01248.myvolleyplus.wrapper;

import android.content.Context;

import com.hss01248.myvolleyplus.volley.MyVolleyUtils;

import java.util.Map;

/**
 * Created by Administrator on 2016/8/30.
 */
public class MyNetUtil {


    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        MyVolleyUtils.getInstance(context);

    }
    public static void postRequest(String urlTail, Object tag, Map params,boolean isForceMinTime,
                                   final MyNetCallback myNetCallback){

    }

    public static void postRequest(String urlTail, Object tag, Map params,int timeOut,int cacheTime,
                                   final MyNetCallback myNetCallback){

    }

    public static void getRequest(final String urlTail, final String tag, int timeOut,int cacheTime, Map map,final MyNetCallback myNetCallback){


    }



    public static void cancleRequest(Object tag){

    }

    public static void autoLogin(){

    }

    public static void autoLogin(final MyNetCallback myNetListener){

    }
}
