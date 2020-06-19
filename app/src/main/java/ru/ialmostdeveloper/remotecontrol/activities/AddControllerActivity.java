package ru.ialmostdeveloper.remotecontrol.activities;

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

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.Session;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.controllers.NECController;
import ru.ialmostdeveloper.remotecontrol.controllers.RC5Controller;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;

public class AddControllerActivity extends AppCompatActivity {

    @Inject
    Storage storage;

    @Inject
    HashMap<String, List<ControllerButton>> controllerPresets;

    @Inject
    RequestsManager requestsManager;

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
                String name = controllerNameInput.getText().toString();
                IController newController = null;
                switch (controllerPresetsSpinner.getSelectedItem().toString()) {
                    case "RC5":
                        newController = new RC5Controller(controllerIdInput.getText().toString(), name,
                                controllerPresets.get("RC5"));
                        break;

                    case "NEC":
                        newController = new NECController(controllerIdInput.getText().toString(), name,
                                controllerPresets.get("NEC"));
                        break;

                    case "None":
                        newController = new RC5Controller(controllerIdInput.getText().toString(), name,
                                controllerPresets.get("None"));
                        break;
                }
                assert newController != null;
                Session session = storage.readSession();
                if (requestsManager.addController(newController, session.login, session.token)) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void setControllersSpinner() {
        controllerPresetsSpinner = findViewById(R.id.controllerPresetsSpinner);
        ArrayList<String> items = new ArrayList<>(controllerPresets.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        controllerPresetsSpinner.setAdapter(adapter);
    }
}
