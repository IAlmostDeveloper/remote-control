package ru.ialmostdeveloper.remotecontrol.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import ru.ialmostdeveloper.remotecontrol.ControllerScript;
import ru.ialmostdeveloper.remotecontrol.R;
import ru.ialmostdeveloper.remotecontrol.di.MyApplication;
import ru.ialmostdeveloper.remotecontrol.mqtt.Storage;
import ru.ialmostdeveloper.remotecontrol.network.RequestsManager;

public class ScriptsActivity extends AppCompatActivity {

    @Inject
    RequestsManager requestsManager;
    @Inject
    Storage storage;

    List<ControllerScript> scripts;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scripts);

        ((MyApplication) getApplication())
                .getAppComponent()
                .inject(this);

        setProgressDialog();
        setScriptsLayout();
        setAddScriptButton();
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(ScriptsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setScriptsLayout() {
        new GetScriptsTask().execute(storage.readSession().login, storage.readSession().token);
    }

    private void setAddScriptButton() {
        Button addScriptButton = findViewById(R.id.addScriptButton);
        addScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), CreateScriptActivity.class), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                break;
        }
    }

    class GetScriptsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String user = strings[0];
            String token = strings[1];
            scripts = requestsManager.getUserScripts(user, token);
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            final LinearLayout scriptsLayout = findViewById(R.id.scriptsLayout);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 5, 5, 5);
            for (final ControllerScript script : scripts) {
                final Button button = new Button(getApplicationContext());
                button.setText(script.name);
                button.setLayoutParams(layoutParams);
                button.setBackgroundResource(R.drawable.custombutton);
                button.setTextColor(getResources().getColor(R.color.colorText));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ExecuteScriptTask().execute(String.valueOf(script.id), storage.readSession().token);
                    }
                });
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(ScriptsActivity.this, android.R.style.Theme_Material_Dialog_Alert)
                                .setTitle("Delete script?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        scriptsLayout.removeView(button);
                                        new DeleteScriptTask().execute(storage.readSession().token,
                                                storage.readSession().login, button.getText().toString());
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
                scriptsLayout.addView(button);
            }
            progressDialog.dismiss();
            super.onPostExecute(aBoolean);
        }
    }

    class ExecuteScriptTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            int scriptId = Integer.parseInt(strings[0]);
            String token = strings[1];
            return requestsManager.executeScript(scriptId, token);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Executing script...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
        }
    }

    class DeleteScriptTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            String token = strings[0];
            String user = strings[1];
            String name = strings[2];
            return requestsManager.deleteScript(token, user, name);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Removing script...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
        }


    }
}
