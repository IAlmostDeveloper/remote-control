package ru.ialmostdeveloper.remotecontrol;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
}
