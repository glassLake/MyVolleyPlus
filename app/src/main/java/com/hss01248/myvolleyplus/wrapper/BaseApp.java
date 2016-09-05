package com.hss01248.myvolleyplus.wrapper;

import android.app.Application;

import com.hss01248.myvolleyplus.retrofit.MyRetrofitUtil;

/**
 * Created by Administrator on 2016/9/4.
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyRetrofitUtil.init(getApplicationContext());

    }


}
