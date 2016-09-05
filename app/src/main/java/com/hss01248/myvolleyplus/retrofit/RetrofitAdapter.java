package com.hss01248.myvolleyplus.retrofit;

import android.util.Log;

import com.hss01248.myvolleyplus.config.ConfigInfo;
import com.hss01248.myvolleyplus.config.NetConfig;
import com.hss01248.myvolleyplus.volley.MyVolleyUtils;
import com.hss01248.myvolleyplus.wrapper.CommonHelper;
import com.hss01248.myvolleyplus.wrapper.MyNetCallback;
import com.hss01248.myvolleyplus.wrapper.NetAdapter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class RetrofitAdapter extends NetAdapter<Call> {

    Retrofit retrofit;
    ApiService service;



    private void init() {
        //默认情况下，Retrofit只能够反序列化Http体为OkHttp的ResponseBody类型
        //并且只能够接受ResponseBody类型的参数作为@body

        OkHttpClient.Builder httpBuilder=new OkHttpClient.Builder();
        OkHttpClient client=httpBuilder.readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS) //设置超时
                .retryOnConnectionFailure(true)//重试
                .addInterceptor(new ProgressInterceptor())//下载时更新进度
                .build();

        retrofit = new Retrofit
                .Builder()
                .baseUrl(NetConfig.baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // 使用Gson作为数据转换器
                .build();

        service = retrofit.create(ApiService.class);
    }

    private static RetrofitAdapter instance;

    private RetrofitAdapter(){
       init();
    }

    public static  RetrofitAdapter getInstance(){
        if (instance == null){
            synchronized (MyVolleyUtils.class){
                if (instance ==  null){
                    instance = new RetrofitAdapter();
                }
            }
        }
        return  instance;
    }


    @Override
    protected void addToQunue(Call request) {
        //空实现即可

    }

    @Override
    protected void cacheControl(ConfigInfo configInfo, Call request) {
        //todo

    }

    @Override
    protected Call newSingleUploadRequest(int method, String url, Map map, ConfigInfo configInfo, MyNetCallback myListener) {
        return null;
    }

    @Override
    protected Call newDownloadRequest(int method, String url, Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {

        Call<ProgressResponseBody> call = service.download(url);
        myListener.registEventBus();



        call.enqueue(new Callback<ProgressResponseBody>() {
            @Override
            public void onResponse(Call<ProgressResponseBody> call, Response<ProgressResponseBody> response) {
                Log.e("download","onResponse finished");
                //开子线程将文件写到指定路径中
                CommonHelper.writeFile(response.body().byteStream(),configInfo.filePath,myListener);
            }

            @Override
            public void onFailure(Call<ProgressResponseBody> call, Throwable t) {
                myListener.onError(t.toString());
            }
        });
        return call;
    }

    @Override
    protected Call newStringRequest(final int method, final String url, final Map map, final ConfigInfo configInfo, final MyNetCallback myListener) {

        Log.e("url","newStringRequest:"+url);
        //todo 分方法:
        Call<String> call;

        if (method == NetConfig.Method.GET){
            call = service.executGet(url,map);
        }else if (method == NetConfig.Method.POST){
            call = service.executePost(url,map);
        }else {
            call = null;
        }


        final long time = System.currentTimeMillis();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                CommonHelper.parseStringResponseInTime(time,response.body(),method,url,map,configInfo,myListener);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                CommonHelper.parseErrorInTime(time,t.toString(),configInfo,myListener);
            }
        });
        return call;
    }

    @Override
    protected void setInfoToRequest(ConfigInfo configInfo, Call request) {




    }

    @Override
    public void cancelRequest(Object tag) {


    }
}
