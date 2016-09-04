package com.hss01248.myvolleyplus.config;

/**
 * Created by Administrator on 2016/8/30.
 */
public class NetBaseBean<T> {
    public int code;
    public String msg;
    public T data;

    //TODO
    public static final int CODE_NONE = -1;
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_UNLOGIN = 2;
    public static final int CODE_UN_FOUND = 3;

}
