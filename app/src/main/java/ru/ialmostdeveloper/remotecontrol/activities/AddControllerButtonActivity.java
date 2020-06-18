package ru.ialmostdeveloper.remotecontrol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AddControllerButtonActivity extends AppCompatActivity {

    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    Storage storage;

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
        setReceiverButton();
        setInputs();
    }

    private void setReceiverButton() {
//        Button receiverButton = findViewById(R.id.getCodeFromReceiverButton);
//        receiverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String requestTopic = "remoteControl/devices/" + getIntent().getStringExtra("deviceId") + "/receive";
//                String responseTopic = mqttManager.getClient().getClientId();
//                try {
//                    mqttManager.getClient().subscribe(responseTopic, 0);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//                mqttManager.getClient().setCallback(new MqttCallback() {
//                    @Override
//                    public void connectionLost(Throwable cause) {
//
//                    }
//
//                    @Override
//                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        JSONObject obj = new JSONObject(new String(message.getPayload()));
//                        long value = Long.parseLong(obj.get("code").toString());
//                        String receivedCode = Long.toHexString(value);
//                        buttonCodeInput.setText("0x" + receivedCode);
//                    }
//
//                    @Override
//                    public void deliveryComplete(IMqttDeliveryToken token) {
//
//                    }
//                });
//                MqttMessage responseMessage = new MqttMessage();
//                responseMessage.setPayload(responseTopic.getBytes());
//                try {
//                    mqttManager.getClient().publish(requestTopic, responseMessage);
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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
