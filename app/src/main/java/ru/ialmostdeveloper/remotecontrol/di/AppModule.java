package ru.ialmostdeveloper.remotecontrol.di;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.RC5Controller;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttStorage;

@Module
class AppModule {
    AppModule(){

    }

    private HashMap<String, Integer>  RC5StandardControls = new HashMap<String, Integer>(){{
        put("TurnOn", 0x80C);
        put("Channel1", 0x801);
        put("Previous", 0x820);
        put("Next", 0x821);
    }
    };

    @Provides
    HashMap<String, IController> provideControllersList(){
        HashMap<String, IController> controllersList = new HashMap<>();
        controllersList.put("RC5", new RC5Controller(RC5StandardControls));
        return controllersList;
    }

    @Provides
    HashMap<String, Integer> provideRC5StandardControls(){
        return RC5StandardControls;
    }

    @Provides
    String provideMqttHost(){
        return "tcp://tailor.cloudmqtt.com:12878";
    }

    @Provides
    MqttAndroidClient provideMqttClient(Context context, String mqttHost){
        return new MqttAndroidClient(context,
                mqttHost, "ExampleClientID" + System.currentTimeMillis());
    }

    @Provides
    MqttConnectOptions provideMqttConnectOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setUserName("pyhyvrkj");
        options.setPassword("yc_uB67FT97v".toCharArray());
        return options;
    }

    @Provides
    List<String> provideMqttTopicsList(){
        ArrayList<String> topics = new ArrayList<>();
        topics.add("devices/1/remote");
        return topics;
    }

    @Provides
    MqttStorage provideMqttStorage(Context context){
        return new MqttStorage(context);
    }

    @Provides
    MqttManager provideMqttManager(MqttStorage mqttStorage, MqttAndroidClient mqttClient, MqttConnectOptions mqttConnectOptions,
                                   List<String> mqttTopicsList){
        return new MqttManager(mqttStorage, mqttClient, mqttConnectOptions, mqttTopicsList);
    }
}
