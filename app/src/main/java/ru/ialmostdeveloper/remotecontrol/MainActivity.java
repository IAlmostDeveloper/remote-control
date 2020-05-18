package ru.ialmostdeveloper.remotecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.MqttManager;


public class MainActivity extends AppCompatActivity {
    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    MqttManager mqttManager;

    Spinner controllersSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setSettingsButton();
        setControllersSpinner();
        setControlsLayout();
    }

    private void setControlsLayout() {
        LinearLayout controlsLayout = findViewById(R.id.buttonsLayout);
        controlsLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final ControllerButton buttonName :
                Objects.requireNonNull(controllersList
                        .get(controllersSpinner.getSelectedItem()))
                        .getControlButtons()) {
            Button button = new Button(this);
            button.setText(buttonName.name);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mqttManager.sendButtonCode(
                            controllersList.get(controllersSpinner.getSelectedItem()).getDeviceId(), buttonName.code);
                }
            });
            controlsLayout.addView(button);
        }
    }

    private void setControllersSpinner() {
        controllersSpinner = findViewById(R.id.controllersSpinner);
        ArrayList<String> items = new ArrayList<>(controllersList.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        controllersSpinner.setAdapter(adapter);
        controllersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setControlsLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSettingsButton() {
        final Button settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    }
}
