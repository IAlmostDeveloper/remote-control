package ru.ialmostdeveloper.remotecontrol.activities;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_controller_button);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        new GetControllersTask().execute();
    }

    private void setReceiverButton() {
        Button receiverButton = findViewById(R.id.getCodeFromReceiverButton);
        receiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long value = requestsManager.receiveCode(getIntent().getStringExtra("deviceId"));
                String receivedCode = Long.toHexString(value);
                buttonCodeInput.setText("0x" + receivedCode);
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
        }
    }
}
