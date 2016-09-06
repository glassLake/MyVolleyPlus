package com.hss01248.myvolleyplus.wrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Administrator on 2016/4/24.
 */
public class MyJson {

    public static String toJsonStr(Object obj){
        return new Gson().toJson(obj);
        // return JSON.parseObject(str,clazz);
    }


    public static <T> T  parseObject(String str,Class<T> clazz){
        return new Gson().fromJson(str,clazz);
        // return JSON.parseObject(str,clazz);
    }

    public static  <E> List<E> parseArray(String str,Class<E> clazz){
        return new Gson().fromJson(str,new TypeToken<List<E>>() {}.getType());
        // return JSON.parseArray(str,clazz);
    }


}
