package ru.ialmostdeveloper.remotecontrol;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AuthActivity extends AppCompatActivity {

    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

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
        Session session = storage.readSession();
        loginInput.setText(session.login);
        passwordInput.setText(session.password);

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
                if (!requestsManager.register(login, password))
                    Toast.makeText(getApplicationContext(), "User is already registered", Toast.LENGTH_SHORT).show();
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
                String token = requestsManager.auth(login, password);
                if (!token.equals("")) {
                    storage.writeSession(new Session(login, password, token, true));
                    setResult(RESULT_OK);
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), "Incorrect user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(storage.readSession().isValid)
            super.onBackPressed();
    }
}
