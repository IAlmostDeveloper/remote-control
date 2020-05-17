package ru.ialmostdeveloper.remotecontrol.controllers;

import java.util.HashMap;

public class TVController implements IController{

    private HashMap<String, Integer> controlButtons;

    public TVController(HashMap<String, Integer> controlButtons){
        setControlButtons(controlButtons);
    }

    @Override
    public HashMap<String, Integer> getControlButtons() {
        return controlButtons;
    }

    @Override
    public void setControlButtons(HashMap<String, Integer> controlButtons) {
        this.controlButtons = controlButtons;
    }

    @Override
    public int getControlButtonCode(String name) {
        if(!controlButtons.containsKey(name))
            throw new IllegalArgumentException("Key is not in HashMap: " + name);
        return controlButtons.get(name);

    }

    @Override
    public void setControlButtonCode(String name, int code) {
        controlButtons.put(name, code);
    }
}
