package ru.ialmostdeveloper.remotecontrol.controllers;

import java.util.List;

public interface IController {

    List<ControllerButton> getControlButtons();

    String getDeviceId();

    void setControlButtons(List<ControllerButton> controlButtons);

    void addControllerButton(ControllerButton button);

    long getControlButtonCode(String name);

    void setControlButtonCode(String name, int code);
}
