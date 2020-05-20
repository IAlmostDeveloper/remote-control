package ru.ialmostdeveloper.remotecontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.NECController;
import ru.ialmostdeveloper.remotecontrol.controllers.RC5Controller;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;

public class AddControllerActivity extends AppCompatActivity {

    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    MqttManager mqttManager;
    @Inject
    HashMap<String, List<ControllerButton>> controllerPresets;

    EditText controllerNameInput;
    EditText controllerIdInput;
    Spinner controllerPresetsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_controller);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setInputs();
        setControllersSpinner();
        setAddControllerButton();
    }

    private void setInputs() {
        controllerNameInput = findViewById(R.id.addControllerNameInput);
        controllerIdInput = findViewById(R.id.addControllerIdInput);
    }

    private void setAddControllerButton() {
        Button button = findViewById(R.id.addControllerApply);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IController newController = null;
                switch(controllerPresetsSpinner.getSelectedItem().toString()){
                    case "RC5":
                        newController = new RC5Controller(controllerIdInput.getText().toString(),
                                controllerPresets.get("RC5"));
                        break;

                    case "NEC":
                        newController = new NECController(controllerIdInput.getText().toString(),
                                controllerPresets.get("NEC"));
                        break;

                    case "None":
                        newController = new RC5Controller(controllerIdInput.getText().toString(),
                                controllerPresets.get("None"));
                        break;
                }
                controllersList.put(controllerNameInput.getText().toString(), newController);
                mqttManager.getStorage().writeControllers(controllersList);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void setControllersSpinner() {
        controllerPresetsSpinner = findViewById(R.id.controllerPresetsSpinner);
        ArrayList<String> items = new ArrayList<>(controllerPresets.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        controllerPresetsSpinner.setAdapter(adapter);
    }
}
