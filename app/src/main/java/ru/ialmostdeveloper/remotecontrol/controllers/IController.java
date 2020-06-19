package ru.ialmostdeveloper.remotecontrol.controllers;

import java.util.List;

public interface IController extends Convertable{

    List<ControllerButton> getControlButtons();

    String getDeviceId();

    String getName();

    void setControlButtons(List<ControllerButton> controlButtons);

    void addControllerButton(ControllerButton button);

    void removeControllerButton(String name);

    long getControlButtonCode(String name);
}
