package ru.ialmostdeveloper.remotecontrol.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import ru.ialmostdeveloper.remotecontrol.activities.AddControllerActivity;
import ru.ialmostdeveloper.remotecontrol.activities.AddControllerButtonActivity;
import ru.ialmostdeveloper.remotecontrol.AuthActivity;
import ru.ialmostdeveloper.remotecontrol.activities.MainActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AuthActivity authActivity);

    void inject(AddControllerActivity addControllerActivity);

    void inject(AddControllerButtonActivity addControllerButtonActivity);

    @Component.Builder interface Builder{
        AppComponent build();

        @BindsInstance
        Builder context(Context context);

        @BindsInstance
        Builder appModule(AppModule appModule);
    }

}
