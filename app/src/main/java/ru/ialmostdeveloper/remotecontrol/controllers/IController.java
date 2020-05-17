package ru.ialmostdeveloper.remotecontrol.controllers;

import java.util.HashMap;

public interface IController {
    HashMap<String, Integer> getControlButtons();
    void setControlButtons(HashMap<String, Integer> controlButtons);
    int getControlButtonCode(String name);
    void setControlButtonCode(String name, int code);
}
