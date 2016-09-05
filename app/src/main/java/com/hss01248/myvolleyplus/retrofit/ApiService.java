package com.hss01248.myvolleyplus.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public interface  ApiService {


    /**
     * 注意: retrofit默认转换string成json obj,如果不需要gson转换,那么就指定泛型为ResponseBody或其子类
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @POST()
    Call<ResponseBody> executePost(@Url String url, @FieldMap Map<String, String> maps);

    @GET()
    Call<ResponseBody> executGet(@Url String url, @QueryMap Map<String, String> maps);

   // @Streaming
    @GET
    Call<ResponseBody> download(@Url String fileUrl);




    @GET()
    Call<String> get(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @GET()
    Call<String> getArray(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Call<String> post(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Call<String> postArray(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);

/*

    Observable<String> Obget(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @GET()
    Observable<jsonarray> ObgetArray(@Url String url, @QueryMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Observable<String> Obpost(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);

    @FormUrlEncoded
    @POST()
    Observable<jsonarray> ObpostArray(@Url String url, @FieldMap Map<String, String> params, @Header("Cache-Time") String time);*/

    //post json:  http://blog.csdn.net/qqyanjiang/article/details/50948908








}
