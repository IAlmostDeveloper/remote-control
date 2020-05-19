package ru.ialmostdeveloper.remotecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;

public class AddControllerButtonActivity extends AppCompatActivity {

    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    MqttManager mqttManager;

    EditText buttonNameInput;
    EditText buttonCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_controller_button);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setAddButton();
        setInputs();
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
}
