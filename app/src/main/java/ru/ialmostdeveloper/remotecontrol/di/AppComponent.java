package ru.ialmostdeveloper.remotecontrol.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.ialmostdeveloper.remotecontrol.activities.AddControllerActivity;
import ru.ialmostdeveloper.remotecontrol.activities.AddControllerButtonActivity;
import ru.ialmostdeveloper.remotecontrol.activities.AuthActivity;
import ru.ialmostdeveloper.remotecontrol.activities.CreateScriptActivity;
import ru.ialmostdeveloper.remotecontrol.activities.MainActivity;
import ru.ialmostdeveloper.remotecontrol.activities.ScriptsActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AuthActivity authActivity);

    void inject(AddControllerActivity addControllerActivity);

    void inject(AddControllerButtonActivity addControllerButtonActivity);

    void inject(ScriptsActivity scriptsActivity);

    void inject(CreateScriptActivity createScriptActivity);

    @Component.Builder interface Builder{
        AppComponent build();

        @BindsInstance
        Builder context(Context context);

        @BindsInstance
        Builder appModule(AppModule appModule);
    }

}
