package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import ru.ialmostdeveloper.remotecontrol.ControllerScript;
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
    StringBuilder scriptSequence;
    LinearLayout scriptLayout;
    Dialog selectButtonDialog;
    ProgressDialog progressDialog;
    AlertDialog scriptNameDialog;
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
        scriptLayout = findViewById(R.id.scriptsLayout);
        setSelectButtonDialog();
        setProgressDialog();
        setScriptNameDialog();
        setAddButtonButton();
        setCreateScriptButton();
        setApplyDialogButton();


    }

    private void setScriptNameDialog() {
        LinearLayout layout = new LinearLayout(CreateScriptActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(CreateScriptActivity.this);
        textView.setText("Enter your script name:");
        final EditText input = new EditText(CreateScriptActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        layout.addView(textView);
        layout.addView(input);

        scriptNameDialog = new AlertDialog.Builder(CreateScriptActivity.this).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AddScriptTask().execute(input.getText().toString(), scriptSequence.toString());
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        scriptNameDialog.setView(layout);
    }

    private void setApplyDialogButton() {
        Button applyButton = dialogLayout.findViewById(R.id.applyDialogButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText repeatsCountInput = dialogLayout.findViewById(R.id.repeatsCountInput);
                EditText delayInput = dialogLayout.findViewById(R.id.delayInput);
                Button selectedButton = dialogLayout.findViewById(R.id.selectedButton);
                TextView selectedButtonCode = dialogLayout.findViewById(R.id.selectedButtonCodeLabel);
                TextView controllerName = dialogLayout.findViewById(R.id.selectedControllerLabel);
                if (!repeatsCountInput.getText().toString().isEmpty()
                        && !delayInput.getText().toString().isEmpty()
                        && !selectedButtonCode.getText().toString().equals("Code")) {
                    ControllerButton controllerButton =
                            new ControllerButton(selectedButton.getText().toString(),
                                    Long.parseLong(selectedButtonCode.getText().toString()));
                    addStepToScript(Objects.requireNonNull(controllersList.get(controllerName.getText().toString())),
                            controllerButton,
                            repeatsCountInput.getText().toString(),
                            delayInput.getText().toString());
                    addStepToScriptLayout(Objects.requireNonNull(controllersList.get(controllerName.getText().toString())),
                            controllerButton,
                            repeatsCountInput.getText().toString(),
                            delayInput.getText().toString());
                    selectButtonDialog.dismiss();
                }
            }
        });
    }

    private void addStepToScriptLayout(IController controller, ControllerButton controllerButton, String repeatsCount, String delay) {
        ConstraintLayout stepLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.script_step_layout, null);
        Button stepButton = stepLayout.findViewById(R.id.stepButton);
        TextView repeatsCountLabel = stepLayout.findViewById(R.id.repeatsCountLabel);
        TextView delayLabel = stepLayout.findViewById(R.id.delayLabel);
        TextView pressLabel = stepLayout.findViewById(R.id.pressLabel);
        pressLabel.setText("Press " + controller.getName() + ":");
        stepButton.setText(controllerButton.name);
        repeatsCountLabel.setText(repeatsCount + " times");
        delayLabel.setText("Wait for " + delay + " ms");
        scriptLayout.addView(stepLayout);
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
        Button createScriptButton = findViewById(R.id.createScriptButton);
        createScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scriptSequence = new StringBuilder();
                for (ScriptStep step : scriptSteps) {
                    scriptSequence.append(step.id);
                    scriptSequence.append(";");
                    scriptSequence.append(step.code);
                    scriptSequence.append(";");
                    scriptSequence.append(step.encoding);
                    scriptSequence.append(";");
                    scriptSequence.append(step.repeatCount);
                    scriptSequence.append(";");
                    scriptSequence.append(step.delay);
                    scriptSequence.append(";");
                }
                scriptSequence.deleteCharAt(scriptSequence.length()-1);
                scriptNameDialog.show();
            }
        });
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(CreateScriptActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setSelectButtonDialog() {
        selectButtonDialog = new Dialog(CreateScriptActivity.this);
        dialogLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.select_button_dialog_layout, null);
        setApplyDialogButton();
        selectButtonDialog.setContentView(dialogLayout);
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

    private void addStepToScript(IController controller, ControllerButton button, String repeatsCount, String delay) {
        String id = controller.getDeviceId();
        String code = Long.toHexString(button.code);
        String encoding = controller.getClassName();
        scriptSteps.add(new ScriptStep(id, "0x" + code, encoding, repeatsCount, delay));
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
            setSelectButtonDialog();
            setControllersSpinner();
            setControlsLayout();
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

    class AddScriptTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            ControllerScript script = new ControllerScript(0, strings[0], strings[1]);
            return requestsManager.addScript(script, storage.readSession().login, storage.readSession().token);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Adding script...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }
    }
}
