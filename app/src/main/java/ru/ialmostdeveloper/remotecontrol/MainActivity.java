package ru.ialmostdeveloper.remotecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;


public class MainActivity extends AppCompatActivity {
    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    MqttManager mqttManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication)getApplication())
                .getAppComponent()
                .inject(this);

        StringBuilder text = new StringBuilder();
        for (int entry: controllersList.get("RC5").getControlButtons().values())
            text.append(Integer.toHexString(entry)).append("\n");
        ((TextView)findViewById(R.id.nameTextView)).setText(text.toString());

        findViewById(R.id.nameTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttManager.publish("remote", "fuck yeah it works");
            }
        });
    }
}
