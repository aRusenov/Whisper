package com.example.nasko.whisper.dagger.rest;

import com.example.nasko.whisper.data.rest.UserService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class RestModule {

    private String apiUrl;

    public RestModule(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Provides @RestScope Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Provides @RestScope UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }
}
