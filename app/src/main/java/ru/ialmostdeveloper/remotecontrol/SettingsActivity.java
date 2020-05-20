package ru.ialmostdeveloper.remotecontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.HashMap;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;

public class SettingsActivity extends AppCompatActivity {
    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    MqttManager mqttManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        final EditText address = findViewById(R.id.mqttBrokerAddress_input);
        final EditText username = findViewById(R.id.username_input);
        final EditText password = findViewById(R.id.password_input);

        address.setText(mqttManager.getStorage().readMqttHost());
        MqttConnectOptions options = mqttManager.getStorage().readMqttConnectionOptions();
        username.setText(options.getUserName() == null ? "" : options.getUserName());
        password.setText(options.getPassword() == null ? "" : new String(options.getPassword()));
        Button saveSettingsButton = findViewById(R.id.save_button);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().isEmpty()
                        || username.getText().toString().isEmpty()
                        || password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, fill all settings", Toast.LENGTH_SHORT).show();
                    return;
                }
                mqttManager.getStorage().writeMqttHost(address.getText().toString());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username.getText().toString());
                options.setPassword(password.getText().toString().toCharArray());
                options.setAutomaticReconnect(true);
                options.setCleanSession(false);
                mqttManager.getStorage().writeMqttConnectionOptions(options);
                mqttManager.setConnectOptions(options);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
