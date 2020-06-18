package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.Objects;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.AuthActivity;
import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;


public class MainActivity extends AppCompatActivity {
    @Inject
    HashMap<String, IController> controllersList;
    @Inject
    Storage storage;
    @Inject
    RequestsManager requestsManager;

    Spinner controllersSpinner;
    ArrayAdapter<String> controllersSpinnerAdapter;

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
        setAddControllerButton();
        setApplyDeleteDialog();
    }

    private void setApplyDeleteDialog() {
    }

    private void setSettingsButton() {
        final Button settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AuthActivity.class), 0);
            }
        });
    }

    private void setControllersSpinner() {
        controllersSpinner = findViewById(R.id.controllersSpinner);
        ArrayList<String> items = new ArrayList<>(controllersList.keySet());
        controllersSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        controllersSpinner.setAdapter(controllersSpinnerAdapter);
        controllersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setControlsLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        controllersSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle("Delete controller?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String currentControllerName = controllersSpinner.getSelectedItem().toString();
                                controllersSpinnerAdapter.remove(currentControllerName);
                                controllersList.remove(currentControllerName);
                                storage.writeControllers(controllersList);
                                setControlsLayout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return false;
            }
        });
    }

    private void setControlsLayout() {
        final LinearLayout controlsLayout = findViewById(R.id.buttonsLayout);
        controlsLayout.removeAllViews();
        controlsLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        if (controllersList.size() == 0) return;
        final IController currentController = controllersList
                .get(controllersSpinner.getSelectedItem());
        for (final ControllerButton buttonName :
                Objects.requireNonNull(currentController)
                        .getControlButtons()) {
            final Button button = new Button(this);
            button.setText(buttonName.name);
            button.setLayoutParams(layoutParams);
            button.setBackgroundResource(R.drawable.custombutton);
            button.setTextColor(getResources().getColor(R.color.colorText));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Sending code...");
                    requestsManager.SendCode(Integer.parseInt(currentController.getDeviceId()),
                            String.valueOf(buttonName.code), currentController.getClassName());

                }
            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert)
                            .setTitle("Delete button?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    controlsLayout.removeView(button);
                                    currentController.removeControllerButton(buttonName.name);
                                    storage.writeControllers(controllersList);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                    return false;
                }
            });
            controlsLayout.addView(button);
        }
        Button addButtonButton = findViewById(R.id.addButtonButton);
        addButtonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        AddControllerButtonActivity.class)
                        .putExtra("deviceId", currentController.getDeviceId());
                startActivityForResult(intent, 2);
            }
        });
    }

    private void setAddControllerButton() {
        Button button = findViewById(R.id.addControllerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddControllerActivity.class), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

        }
        switch (requestCode) {
            case 0:
                break;
            case 1:
                setControllersSpinner();
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    String buttonName = data.getStringExtra("ButtonName");
                    long buttonCode = data.getLongExtra("ButtonCode", 0);
                    Objects.requireNonNull(controllersList.get(controllersSpinner
                            .getSelectedItem()
                            .toString()))
                            .addControllerButton(new ControllerButton(buttonName, buttonCode));
                    storage.writeControllers(controllersList);
                    setControlsLayout();
                }
                break;
        }
    }
}
