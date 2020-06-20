package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.network.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AddControllerButtonActivity extends AppCompatActivity {

    HashMap<String, IController> controllersList;
    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

    EditText buttonNameInput;
    EditText buttonCodeInput;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_controller_button);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setProgressDialog();
        new GetControllersTask().execute();
    }

    private void setReceiverButton() {
        Button receiverButton = findViewById(R.id.getCodeFromReceiverButton);
        receiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddReceivedCodeTask().execute(getIntent().getStringExtra("deviceId"));
            }
        });
    }

    private void setInputs() {
        buttonNameInput = findViewById(R.id.addControllerButtonNameInput);
        buttonCodeInput = findViewById(R.id.addControllerButtonCodeInput);
    }

    private void setAddButton() {
        Button addButton = findViewById(R.id.addControllerButtonApply);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ButtonName", buttonNameInput.getText().toString());
                intent.putExtra("ButtonCode", Long.decode(buttonCodeInput.getText().toString()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(AddControllerButtonActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    class GetControllersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            controllersList = requestsManager.getControllers(storage.readSession().login, storage.readSession().token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setAddButton();
            setReceiverButton();
            setInputs();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    class AddReceivedCodeTask extends AsyncTask<String, Boolean, String> {

        @Override
        protected String doInBackground(String... strings) {
            String key = requestsManager.receiveCodeKey(strings[0]);
            System.out.println(key);

            String code = requestsManager.getReceivedCode(storage.readSession().token, key);
            while (code.equals("")) {
                code = requestsManager.getReceivedCode(storage.readSession().token, key);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(code);
            return code;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Waiting for code...\nPoint your IR to receiver");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String receivedCode = Long.toHexString(Long.parseLong(s));
            buttonCodeInput.setText("0x" + receivedCode);
            progressDialog.dismiss();
        }
    }
}
