package ru.ialmostdeveloper.remotecontrol.mqtt;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import ru.ialmostdeveloper.remotecontrol.Session;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.IControllerInterfaceAdapter;

public class Storage {
    private Context context;

    public Storage(Context context) {
        this.context = context;
    }

    public Session readSession(){
        Session session = new Session();
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "Session.txt");
        StringBuilder sessionRaw = new StringBuilder();
        try {
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;

                while ((i = stream.read()) != -1) {
                    sessionRaw.append((char) i);
                }
                if(!sessionRaw.toString().isEmpty()){
                    session = new Gson().fromJson(sessionRaw.toString(), Session.class);
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
        return session;
    }

    public void writeSession(Session session){
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "Session.txt");
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
            writer.append(new Gson().toJson(session));
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
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(IController.class, new IControllerInterfaceAdapter<IController>());
                    Gson gson = builder.create();
                    Type type = new TypeToken<HashMap<String, IController>>() {
                    }.getType();
                    controllerHashMap = gson.fromJson(mqttControllersRaw.toString(), type);
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
}
