package ru.ialmostdeveloper.remotecontrol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RequestsManager {
    private APIService service;
    private Session session;

    public RequestsManager(APIService service, Session session) {
        this.service = service;
        this.session = session;
    }

    public String auth(String login, String password) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("login", login);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.auth(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if(response.code()==200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                String token = responseBody.get("token").toString();
                String error = responseBody.get("error").toString();
                return error.equals("") ? token : "";
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean register(String login, String password) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("login", login);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.register(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if(response.code()==200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                String error = responseBody.get("error").toString();
                return error.equals("");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean send(int id, String code, String encoding) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", id);
            requestBody.put("code", code);
            requestBody.put("encoding", encoding);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.send(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
