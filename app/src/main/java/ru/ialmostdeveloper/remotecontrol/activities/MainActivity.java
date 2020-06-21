package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;
import ru.ialmostdeveloper.remotecontrol.network.RequestsManager;
import ru.ialmostdeveloper.remotecontrol.network.Session;


public class MainActivity extends AppCompatActivity {

    @Inject
    Storage storage;
    @Inject
    Session session;
    @Inject
    RequestsManager requestsManager;

    ProgressDialog progressDialog;
    HashMap<String, IController> controllersList;
    Spinner controllersSpinner;
    ArrayAdapter<String> controllersSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        if (!session.isValid)
            startActivityForResult(new Intent(getApplicationContext(), AuthActivity.class), 0);

        setProgressDialog();
        setLogoutButton();
        setAddControllerButton();
        setScriptsButton();
        new GetControllersTask().execute();
    }

    private void setScriptsButton() {
        Button scriptsButton = findViewById(R.id.myScriptsButton);
        scriptsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), ScriptsActivity.class), 3);
            }
        });
    }

    private void setLogoutButton() {
        final Button settingsButton = findViewById(R.id.settingsBtn);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.writeSession(new Session());
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
                                new DeleteControllerTask().execute(currentControllerName);
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
                    String code = "0x" + Long.toHexString(buttonName.code);
                    new SendCodeTask().execute(currentController.getDeviceId(),
                           code , currentController.getClassName());

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
                                    new UpdateControllerTask().execute(currentController);
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

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    new GetControllersTask().execute();
                }
                break;
            case 1:
                new GetControllersTask().execute();
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    String buttonName = data.getStringExtra("ButtonName");
                    long buttonCode = data.getLongExtra("ButtonCode", 0);
                    ControllerButton newButton = new ControllerButton(buttonName, buttonCode);
                    IController currentController = controllersList.get(controllersSpinner.getSelectedItem());
                    assert currentController != null;
                    currentController.addControllerButton(newButton);
                    new UpdateControllerTask().execute(currentController);
                }
                break;
            case 3:
                break;
        }
    }

    class GetControllersTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            controllersList = requestsManager.getControllers(storage.readSession().login, storage.readSession().token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setControllersSpinner();
            setControlsLayout();
            progressDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    class UpdateControllerTask extends AsyncTask<IController, Void, Void> {

        @Override
        protected Void doInBackground(IController... iControllers) {
            IController currentController = iControllers[0];
            requestsManager.updateController(currentController, storage.readSession().login, storage.readSession().token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GetControllersTask().execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class DeleteControllerTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String currentControllerName = strings[0];
            requestsManager.deleteController(currentControllerName,
                    storage.readSession().login, storage.readSession().token);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setControlsLayout();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    class SendCodeTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String deviceId = strings[0];
            String code = strings[1];
            String encoding = strings[2];

            return requestsManager.send(Integer.parseInt(deviceId),
                    code, encoding, storage.readSession().token);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (!aBoolean)
                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Sending code...");
            progressDialog.show();
        }
    }
}
