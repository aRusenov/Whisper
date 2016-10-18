package com.example.nasko.whisper.dagger.base;

import android.app.Application;
import android.content.Context;

import com.example.nasko.whisper.data.local.AppUserProvider;
import com.example.nasko.whisper.data.local.LocalUserRepository;
import com.example.nasko.whisper.data.local.UserProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton UserProvider provideUserProvider(Context context) {
        LocalUserRepository localUserRepository = new LocalUserRepository(context);
        return new AppUserProvider(localUserRepository);
    }
}