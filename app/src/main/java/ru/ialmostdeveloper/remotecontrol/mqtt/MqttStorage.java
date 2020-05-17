package ru.ialmostdeveloper.remotecontrol.mqtt;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MqttStorage {
    private Context context;

    public MqttStorage(Context context){
        this.context = context;
    }
    String getMqttHost(){
        File folder = new File(context.getFilesDir(),"mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttHost.txt");
        StringBuilder mqttHost = new StringBuilder();
        try {
            FileInputStream stream = new FileInputStream(file);
            int i;

            while((i=stream.read())!= -1){
                mqttHost.append((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mqttHost.toString();
    }

    void writeMqttHost(String mqttHost){
        File folder = new File(context.getFilesDir(),"mqtt");
        File file = new File(folder.getAbsolutePath() + "MqttHost.txt");
        if(!folder.exists()){
                folder.mkdir();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try{
            File gpxfile = new File(file.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(mqttHost);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
