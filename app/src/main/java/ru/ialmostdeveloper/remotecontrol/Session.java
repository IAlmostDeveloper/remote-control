package ru.ialmostdeveloper.remotecontrol;

public class Session {
    String login;
    String password;
    String token;

    public Session(){
        login = "";
        password = "";
        token = "";
    }

    public Session(String login, String password, String token){
        this.login = login;
        this.password = password;
        this.token = token;
    }
}
