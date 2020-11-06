package ru.ialmostdeveloper.remotecontrol.mqtt;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import ru.ialmostdeveloper.remotecontrol.network.Session;

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

    public String readServerUrl(){
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "ServerUrl.txt");
        String url = "";
        try {
            StringBuilder urlRaw = new StringBuilder();
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                int i;

                while ((i = stream.read()) != -1) {
                    urlRaw.append((char) i);
                }
                if(!urlRaw.toString().isEmpty()){
                    url = urlRaw.toString();
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
        return url.isEmpty() ? "https://ik.remzalp.ru" : url;
    }

    public void writeServerUrl(String newServerUrl){
        File folder = new File(context.getFilesDir(), "mqtt");
        File file = new File(folder.getAbsolutePath() + "ServerUrl.txt");
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
            writer.append(newServerUrl);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
