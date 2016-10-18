package com.example.nasko.whisper.dagger.base;

import com.example.nasko.whisper.data.JsonDeserializer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DeserializationModule {

    @Provides @Singleton
    JsonDeserializer provideJsonDeserializer() {
        return new JsonDeserializer();
    }
}
