package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.managers.AppUserProvider;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.managers.ImageUrlResolver;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.Navigator;

public class WhisperApplication extends Application {

    private UserProvider userProvider;
    private UserService userService;
    private SocketServiceBinder serviceConsumer;
    private Navigator navigator;
    private LocalUserRepository localUserRepository;

    private static WhisperApplication instance;

    public static synchronized WhisperApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        navigator = new Navigator();
        localUserRepository = new LocalUserRepository(getApplicationContext());
        userService = new HerokuUserService(getApplicationContext());
        serviceConsumer = new SocketServiceBinder(getApplicationContext());
        userProvider = new AppUserProvider();
        ImageUrlResolver.setEndpoint(
                ConfigLoader.getConfigValue(this, "api_images")
        );
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public LocalUserRepository getLocalUserRepository() {
        return localUserRepository;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public UserService getUserService() {
        return userService;
    }

    public SocketServiceBinder getServiceConsumer() {
        return serviceConsumer;
    }
}
