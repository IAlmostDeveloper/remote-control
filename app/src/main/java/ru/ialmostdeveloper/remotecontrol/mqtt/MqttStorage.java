package ru.ialmostdeveloper.remotecontrol.mqtt;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.RC5Controller;

public class MqttStorage {
    private Context context;

    public MqttStorage(Context context) {
        this.context = context;
    }

    public String readMqttHost() {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttHost.txt");
        StringBuilder mqttHost = new StringBuilder();
        try {
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;

                while ((i = stream.read()) != -1) {
                    mqttHost.append((char) i);
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mqttHost.toString();
    }

    public void writeMqttHost(String mqttHost) {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttHost.txt");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File fileToWrite = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(fileToWrite);
            writer.append(mqttHost);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public HashMap<String, IController> readControllers() {
        HashMap<String, IController> controllerHashMap = new HashMap<>();
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttControllersList.txt");
        StringBuilder mqttControllersRaw = new StringBuilder();
        try {
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;

                while ((i = stream.read()) != -1) {
                    mqttControllersRaw.append((char) i);
                }
                if(!mqttControllersRaw.toString().isEmpty()){
                    Type type = new TypeToken<HashMap<String, RC5Controller>>() {
                    }.getType();
                    controllerHashMap = new Gson().fromJson(mqttControllersRaw.toString(), type);
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return controllerHashMap;
    }

    public void writeControllers(HashMap<String, IController> controllerHashMap) {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttControllersList.txt");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File fileToWrite = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(fileToWrite);
            writer.append(new Gson().toJson(controllerHashMap));
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public MqttConnectOptions readMqttConnectionOptions() {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttConnectionOptions.txt");
        StringBuilder mqttOptionsRaw = new StringBuilder();
        MqttConnectOptions options = new MqttConnectOptions();
        try {
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;
                while ((i = stream.read()) != -1) {
                    mqttOptionsRaw.append((char) i);
                }
                if (!mqttOptionsRaw.toString().isEmpty())
                    options = new Gson().fromJson(mqttOptionsRaw.toString(), MqttConnectOptions.class);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return options;
    }

    public void writeMqttConnectionOptions(MqttConnectOptions options) {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttConnectionOptions.txt");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File fileToWrite = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(fileToWrite);
            writer.append(new Gson().toJson(options));
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public List<String> readMqttSubscribeTopicsList() {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttSubscribeTopics.txt");
        StringBuilder mqttOptionsRaw = new StringBuilder();
        ArrayList<String> subscribeTopics = new ArrayList<>();
        try {
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;

                while ((i = stream.read()) != -1) {
                    mqttOptionsRaw.append((char) i);
                }
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                if (!mqttOptionsRaw.toString().isEmpty())
                    subscribeTopics = new Gson().fromJson(mqttOptionsRaw.toString(), type);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subscribeTopics;
    }

    public void writeMqttSubscribeTopicsList(List<String> subscribeTopics) {
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttSubscribeTopics.txt");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            File fileToWrite = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(fileToWrite);
            writer.append(new Gson().toJson(subscribeTopics));
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
