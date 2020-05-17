package ru.ialmostdeveloper.remotecontrol.di;

import android.content.Context;

import dagger.BindsInstance;
import dagger.Component;
import ru.ialmostdeveloper.remotecontrol.MainActivity;

@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainActivity mainActivity);

    @Component.Builder interface Builder{
        AppComponent build();

        @BindsInstance
        Builder context(Context context);

        @BindsInstance
        Builder appModule(AppModule appModule);
    }

}
