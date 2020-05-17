package ru.ialmostdeveloper.remotecontrol.mqtt;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Objects;

public class MqttManager {

    private MqttStorage storage;
    private MqttAndroidClient client;
    private MqttConnectOptions connectOptions;
    private List<String> subscribeTopicsList;

    public MqttManager(MqttStorage storage, MqttAndroidClient client,
                       MqttConnectOptions connectOptions, List<String> topicsList) {
        this.storage = storage;
        this.subscribeTopicsList = topicsList;
        this.client = client;
        this.connectOptions = connectOptions;

        connect();
    }

    public void publish(String topic, String message) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        try {
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            client.connect(connectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        for (String topic : subscribeTopicsList)
                            client.subscribe(topic, 0);
                        client.publish("remoteControlClient",
                                new MqttMessage(("Client " + client.getClientId() + "  connected successfully!").getBytes()));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("debugtag123", Objects.requireNonNull(exception.getMessage()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
