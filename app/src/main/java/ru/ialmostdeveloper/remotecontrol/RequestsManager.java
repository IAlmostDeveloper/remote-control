package ru.ialmostdeveloper.remotecontrol;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestsManager{
    Session session;

    public RequestsManager(Session session) {
        this.session = session;
    }

    public void AddScriptToDB(String name, String userId, String sequence){

    }

    public void ExecuteScript(String id){

    }

    public void SendCode(int id, String code, String encoding){
        new SendCodeTask().execute(String.valueOf(id), code, encoding);
    }

    private class SendCodeTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String myURL = "https://ik.remzalp.ru/send";
            String body = null;
            try {
                URL url = new URL(myURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                JSONObject requestBody   = new JSONObject();
                requestBody.put("id", Integer.valueOf(strings[0]));
                requestBody.put("code", strings[1]);
                requestBody.put("encoding", strings[2]);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(requestBody.toString());
                wr.flush();
                conn.connect();
                body = RequestsManager.getJSONBodyFromResponse(conn);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    private static String getJSONBodyFromResponse(HttpURLConnection conn) throws IOException {
        StringBuilder sb = new StringBuilder();
        int HttpResult = conn.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
        } else {
        }
        return sb.toString();
    }
}
