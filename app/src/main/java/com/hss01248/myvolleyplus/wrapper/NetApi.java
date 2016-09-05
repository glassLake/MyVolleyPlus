package com.hss01248.myvolleyplus.wrapper;

import java.util.Map;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public interface NetApi {

    void getString(String url, Map map, String tag, final MyNetCallback listener);

    void postStandardJsonRequest(String url,Map map, String tag, final MyNetCallback listener);

    void download(String url,String savedpath,MyNetCallback<String> callback);





    void autoLogin();

    void autoLogin( MyNetCallback myNetListener);
}
