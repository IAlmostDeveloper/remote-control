package ru.ialmostdeveloper.remotecontrol;

public class Session {
    public String login;
    public String password;
    public String token;
    public boolean isValid;

    public Session(){
        login = "";
        password = "";
        token = "";
        isValid = false;
    }

    public Session(String login, String password, String token, boolean isValid){
        this.login = login;
        this.password = password;
        this.token = token;
        this.isValid = isValid;
    }
}
