package ru.ialmostdeveloper.remotecontrol;

public class ControllerScript {
    public int id;
    public String sequence;
    public String name;
    public ControllerScript(int id, String name, String sequence){
        this.id = id;
        this.name = name;
        this.sequence = sequence;
    }
}
