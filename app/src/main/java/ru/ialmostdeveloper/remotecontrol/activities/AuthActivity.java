package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;
import ru.ialmostdeveloper.remotecontrol.network.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.network.Session;

public class AuthActivity extends AppCompatActivity {

    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

    EditText loginInput;
    EditText passwordInput;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setProgressDialog();
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
                new RegTask().execute(login, password);
            }
        });
    }


    private void setSignInButton() {
        Button signInButton = findViewById(R.id.signIn_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = loginInput.getText().toString();
                final String password = passwordInput.getText().toString();
                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, fill in login and password fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                new AuthTask().execute(login, password);
            }
        });
    }

    private void setProgressDialog(){
        progressDialog = new ProgressDialog(AuthActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    class AuthTask extends AsyncTask<String, String, String>{

        String login;
        String password;
        @Override
        protected String doInBackground(String... strings) {
            login = strings[0];
            password = strings[1];
            return requestsManager.auth(login, password);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (!s.equals("")) {
                storage.writeSession(new Session(login, password, s, true));
                setResult(RESULT_OK);
                finish();
            } else
                Toast.makeText(getApplicationContext(), "Incorrect user data", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    class RegTask extends AsyncTask<String, Boolean, Boolean>{

        String login;
        String password;

        @Override
        protected Boolean doInBackground(String... strings) {
            login = strings[0];
            password = strings[1];
            return requestsManager.register(login, password);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), aBoolean ? "Registration successful"
                    : "This user is already registered", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
