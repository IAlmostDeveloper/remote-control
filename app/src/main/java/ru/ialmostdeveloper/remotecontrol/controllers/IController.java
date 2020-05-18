package ru.ialmostdeveloper.remotecontrol.controllers;

import java.util.List;

public interface IController {

    List<ControllerButton> getControlButtons();

    String getDeviceId();

    void setControlButtons(List<ControllerButton> controlButtons);

    long getControlButtonCode(String name);

    void setControlButtonCode(String name, int code);
}
