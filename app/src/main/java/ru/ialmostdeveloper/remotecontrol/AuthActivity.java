package ru.ialmostdeveloper.remotecontrol;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AuthActivity extends AppCompatActivity {

    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

    @Inject
    Gson gson;

    @Inject
    Retrofit retrofit;

    @Inject
    APIService service;

    EditText loginInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setInputFields();
        setSignInButton();
        setSignUpButton();
    }

    private void setInputFields() {
        loginInput = findViewById(R.id.login_input);
        passwordInput = findViewById(R.id.password_input);
    }

    private void setSignUpButton() {
        Button signUpButton = findViewById(R.id.signUp_btn);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, fill in login and password fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
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
                        if(error.equals("")){
                            setResult(RESULT_OK);
                        }
                        else
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setSignInButton() {
        Button signInButton = findViewById(R.id.signIn_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, fill in login and password fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
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
                        if(error.equals("")){
                            setResult(RESULT_OK);
                            finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
