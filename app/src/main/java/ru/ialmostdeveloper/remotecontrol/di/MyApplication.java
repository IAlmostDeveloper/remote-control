package ru.ialmostdeveloper.remotecontrol.di;

import android.app.Application;

public class MyApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.appComponent = DaggerAppComponent
                .builder()
                .context(getApplicationContext())
                .appModule(new AppModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
