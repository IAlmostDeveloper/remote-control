package ru.ialmostdeveloper.remotecontrol.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AuthActivity extends AppCompatActivity {

    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

    private Gson gson;

    private Retrofit retrofit;

    EditText loginInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);
        gson = new GsonBuilder().create();
        retrofit = new Retrofit().Builder
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
                requestsManager.Register(login, password);
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
                requestsManager.Auth(login, password);
            }
        });
    }
}
