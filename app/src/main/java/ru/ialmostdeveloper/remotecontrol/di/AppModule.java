package ru.ialmostdeveloper.remotecontrol.di;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttStorage;

@Module
class AppModule {
    AppModule() {

    }

    private List<ControllerButton> RC5StandardControls() {
        List<ControllerButton> controls = new ArrayList<>();
        controls.add(new ControllerButton("TurnOn", 0x80C));
        controls.add(new ControllerButton("Channel1", 0x801));
        controls.add(new ControllerButton("Channel2", 0x802));
        controls.add(new ControllerButton("Channel3", 0x803));
        controls.add(new ControllerButton("Channel4", 0x804));
        controls.add(new ControllerButton("Channel5", 0x805));
        controls.add(new ControllerButton("Channel6", 0x806));
        controls.add(new ControllerButton("Channel7", 0x807));
        controls.add(new ControllerButton("Channel8", 0x808));
        controls.add(new ControllerButton("Channel9", 0x809));
        controls.add(new ControllerButton("Channel0", 0x800));
        controls.add(new ControllerButton("Previous", 0x820));
        controls.add(new ControllerButton("Next", 0x821));
        return controls;
    }

    @Provides
    @Singleton
    HashMap<String, IController> provideControllersList(MqttStorage storage) {
//        HashMap<String, IController> controllersList = new HashMap<>();
//        controllersList.put("RC5", new RC5Controller("1", RC5StandardControls()));
//        return controllersList;
        return storage.readControllers();
    }

    @Provides
    @Singleton
    HashMap<String, List<ControllerButton>> provideControllerPresets(final List<ControllerButton> RC5controlButtons) {
        return new HashMap<String, List<ControllerButton>>() {
            {
                put("RC5", RC5controlButtons);
                put("None", new ArrayList<ControllerButton>());
            }
        };
    }

    @Provides
    @Singleton
    List<ControllerButton> provideRC5StandardControls() {
        return RC5StandardControls();
    }

    @Provides
    String provideMqttHost(MqttStorage storage) {
        return storage.readMqttHost();
    }

    @Provides
    @Singleton
    MqttAndroidClient provideMqttClient(Context context, String mqttHost) {
        return new MqttAndroidClient(context,
                mqttHost, String.valueOf(System.currentTimeMillis()));
    }

    @Provides
    @Singleton
    MqttConnectOptions provideMqttConnectOptions(MqttStorage storage) {
        return storage.readMqttConnectionOptions();
    }

    @Provides
    @Singleton
    List<String> provideMqttSubscribeTopicsList(MqttStorage storage) {
        return storage.readMqttSubscribeTopicsList();
    }

    @Provides
    @Singleton
    MqttStorage provideMqttStorage(Context context) {
        return new MqttStorage(context);
    }

    @Provides
    MqttManager provideMqttManager(MqttStorage mqttStorage, MqttAndroidClient mqttClient, MqttConnectOptions mqttConnectOptions,
                                   List<String> mqttSubscribeTopicsList) {
        return new MqttManager(mqttStorage, mqttClient, mqttConnectOptions, mqttSubscribeTopicsList);
    }
}
