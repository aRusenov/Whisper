package com.example.nasko.whisper.dagger.base;

import com.example.nasko.whisper.AppState;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppStateModule {

    @Provides @Singleton AppState provideAppState() {
        return new AppState();
    }
}
