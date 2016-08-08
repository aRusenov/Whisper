package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.managers.AppUserProvider;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.managers.ImageUrlResolver;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceConsumer;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;

public class WhisperApplication extends Application {

    private UserProvider userProvider;
    private UserService userService;
    private SocketServiceConsumer serviceConsumer;

    private static WhisperApplication instance;

    public static synchronized WhisperApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        userService = new HerokuUserService(getApplicationContext());
        serviceConsumer = new SocketServiceConsumer(getApplicationContext());
        userProvider = new AppUserProvider();
        ImageUrlResolver.setEndpoint(
                ConfigLoader.getConfigValue(this, "api_images")
        );
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public UserService getUserService() {
        return userService;
    }

    public SocketServiceConsumer getServiceConsumer() {
        return serviceConsumer;
    }
}
