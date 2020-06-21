package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.controllers.ControllerButton;
import ru.ialmostdeveloper.remotecontrol.controllers.IController;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;
import ru.ialmostdeveloper.remotecontrol.network.RequestsManager;

public class CreateScriptActivity extends AppCompatActivity {

    @Inject
    RequestsManager requestsManager;
    @Inject
    Storage storage;

    HashMap<String, IController> controllersList;
    LinearLayout scriptLayout;
    Dialog selectButtonDialog;
    ProgressDialog progressDialog;
    Spinner controllersSpinner;
    ConstraintLayout dialogLayout;
    ArrayAdapter<String> controllersSpinnerAdapter;
    List<ScriptStep> scriptSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_script);

        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        scriptSteps = new ArrayList<>();
        setSelectButtonDialog();
        setProgressDialog();
        setAddButtonButton();
        setCreateScriptButton();
        setApplyDialogButton();


    }

    private void setApplyDialogButton() {
        Button applyButton = dialogLayout.findViewById(R.id.applyDialogButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText repeatsCountInput = dialogLayout.findViewById(R.id.repeatsCountInput);
                EditText delayInput = dialogLayout.findViewById(R.id.delayInput);
                TextView selectedButtonCode = dialogLayout.findViewById(R.id.selectedButtonCodeLabel);
                TextView controllerName = dialogLayout.findViewById(R.id.selectedControllerLabel);
                if (!repeatsCountInput.getText().toString().isEmpty()
                        && !delayInput.getText().toString().isEmpty()
                        && !selectedButtonCode.getText().toString().equals("Code")) {
                    addStepToScript(Objects.requireNonNull(controllersList.get(controllerName.getText().toString())),
                            selectedButtonCode.getText().toString(),
                            repeatsCountInput.getText().toString(),
                            delayInput.getText().toString());
                    selectButtonDialog.dismiss();
                }
            }
        });
    }

    private void setAddButtonButton() {
        Button addButtonButton = findViewById(R.id.addButtonButton);
        addButtonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetControllersTask().execute();
            }
        });
    }

    private void setCreateScriptButton() {

    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(CreateScriptActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setSelectButtonDialog() {
        selectButtonDialog = new Dialog(CreateScriptActivity.this);
        dialogLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.select_button_dialog_layout, null);
    }

    private void setControllersSpinner() {

        controllersSpinner = dialogLayout.findViewById(R.id.controllersSpinner);
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
    }

    private void setControlsLayout() {
        final LinearLayout controlsLayout = dialogLayout.findViewById(R.id.buttonsLayout);
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
                    Button selectedButton = dialogLayout.findViewById(R.id.selectedButton);
                    TextView selectedButtonCodeLabel = dialogLayout.findViewById(R.id.selectedButtonCodeLabel);
                    TextView controllerLabel = dialogLayout.findViewById(R.id.selectedControllerLabel);
                    selectedButton.setText(buttonName.name);
                    selectedButtonCodeLabel.setText(String.valueOf(buttonName.code));
                    controllerLabel.setText(currentController.getName());
                }
            });
            controlsLayout.addView(button);
        }
    }

    private void addStepToScript(IController controller, String buttonCode, String repeatsCount, String delay) {
        String id = controller.getDeviceId();
        String code = String.valueOf(buttonCode);
        String encoding = controller.getClassName();
        scriptSteps.add(new ScriptStep(id, code, encoding, repeatsCount, delay));
    }

    class ScriptStep {
        String id;
        String code;
        String encoding;
        String repeatCount;
        String delay;

        public ScriptStep(String id, String code, String encoding, String repeatCount, String delay) {
            this.id = id;
            this.code = code;
            this.encoding = encoding;
            this.repeatCount = repeatCount;
            this.delay = delay;
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
            selectButtonDialog = new Dialog(CreateScriptActivity.this);
            setControllersSpinner();
            setControlsLayout();
            selectButtonDialog.setContentView(dialogLayout);
            selectButtonDialog.show();
            selectButtonDialog.getWindow().setLayout(700, 1100);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }
}
