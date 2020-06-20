package ru.ialmostdeveloper.remotecontrol.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.NECController;
import ru.ialmostdeveloper.remotecontrol.controllers.RC5Controller;

public class RequestsManager {
    private APIService service;

    public RequestsManager(APIService service) {
        this.service = service;
    }

    public String auth(String login, String password) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("login", login);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.auth(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                String token = responseBody.get("token").toString();
                String error = responseBody.get("error").toString();
                return error.equals("") ? token : "";
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean register(String login, String password) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("login", login);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.register(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                String error = responseBody.get("error").toString();
                return error.equals("");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean send(int id, String code, String encoding, String token) {
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", id);
            requestBody.put("code", code);
            requestBody.put("encoding", encoding);
            requestBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.send(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String receiveCodeKey(String controllerId){
        JSONObject requestBody = new JSONObject();

        try {
            String requestTopic = "remoteControl/devices/" + controllerId + "/receive";
            String responseTopic = "Client" + new Random().nextLong();
            requestBody.put("requestTopic", requestTopic);
            requestBody.put("responseTopic", responseTopic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.receive(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String rawBody = response.body().string();

                return new JSONObject(rawBody).get("key").toString();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getReceivedCode(String token, String key){
        Call<ResponseBody> call = service.receivedCode(token, key);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                return responseBody.getString("code");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public HashMap<String, IController> getControllers(String username, String token) {
        HashMap<String, IController> controllersHashMap = new HashMap<>();
        Call<ResponseBody> call = service.controllers(username, token);
        try {
            Response<ResponseBody> response = call.execute();
            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                JSONArray controllersArray = responseBody.getJSONArray("controllers");

                for (int i = 0; i < controllersArray.length(); i++) {
                    JSONObject a = new JSONObject(controllersArray.get(i).toString());
                    String id = a.getString("id");
                    String name = a.getString("name");
                    String controllerId = a.getString("controllerId");
                    String encoding = a.getString("encoding");
                    String[] buttons = a.getString("buttons").split(";");
                    List<ControllerButton> buttonsList = new ArrayList<>();
                    if (buttons.length >= 2)
                        for (int j = 0; j < buttons.length; j += 2) {
                            ControllerButton button = new ControllerButton(buttons[j], Long.decode(buttons[j + 1]));
                            buttonsList.add(button);
                        }
                    IController controller = encoding.equals("RC5Controller") ? new RC5Controller(controllerId, name, buttonsList)
                            : new NECController(controllerId, name, buttonsList);
                    controllersHashMap.put(name, controller);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return controllersHashMap;
    }

    public boolean addController(IController controller, String login, String token) {
        JSONObject requestBody = new JSONObject();
        StringBuilder buttons = new StringBuilder();
        for (ControllerButton button : controller.getControlButtons()) {
            buttons.append(button.name);
            buttons.append(";");
            buttons.append(button.code);
            buttons.append(";");
        }
        try {
            requestBody.put("token", token);
            requestBody.put("name", controller.getName());
            requestBody.put("user", login);
            requestBody.put("controllerId", controller.getDeviceId());
            requestBody.put("encoding", controller.getClassName());
            requestBody.put("buttons", buttons.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.addController(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                System.out.println(responseBody.toString());
                return true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateController(IController controller, String login, String token) {
        JSONObject requestBody = new JSONObject();
        StringBuilder buttons = new StringBuilder();
        for (ControllerButton button : controller.getControlButtons()) {
            buttons.append(button.name);
            buttons.append(";");
            buttons.append(button.code);
            buttons.append(";");
        }
        try {
            requestBody.put("token", token);
            requestBody.put("name", controller.getName());
            requestBody.put("user", login);
            requestBody.put("buttons", buttons.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.updateController(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                System.out.println(responseBody.toString());
                return true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteController(String controllerName, String login, String token) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", token);
            requestBody.put("name", controllerName);
            requestBody.put("user", login);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());
        Call<ResponseBody> call = service.deleteController(bodyRequest);

        try {
            Response<ResponseBody> response = call.execute();

            if (response.code() == 200) {
                String bodyraw = response.body().string();
                JSONObject responseBody = new JSONObject(bodyraw);
                System.out.println(responseBody.toString());
                return true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
