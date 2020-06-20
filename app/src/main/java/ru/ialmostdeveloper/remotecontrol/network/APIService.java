package ru.ialmostdeveloper.remotecontrol.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST("/auth")
    Call<ResponseBody> auth(@Body RequestBody body);

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST("/register")
    Call<ResponseBody> register(@Body RequestBody body);

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST("/send")
    Call<ResponseBody> send(@Body RequestBody body);

    @POST("/receive")
    Call<ResponseBody> receive(@Body RequestBody body);

    @GET("/controllers")
    Call<ResponseBody> controllers(@Query("user") String user, @Query("token") String token);

    @POST("/add/controller")
    Call<ResponseBody> addController(@Body RequestBody body);

    @POST("/update/controller")
    Call<ResponseBody> updateController(@Body RequestBody body);

    @POST("delete/controller")
    Call<ResponseBody> deleteController(@Body RequestBody body);
}
