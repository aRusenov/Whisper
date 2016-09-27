package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.managers.AppState;
import com.example.nasko.whisper.managers.AppUserProvider;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.MessageNotificationController;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.Navigator;

public class WhisperApplication extends Application {

    private AppState appState;
    private UserProvider userProvider;
    private UserService userService;
    private SocketServiceBinder serviceBinder;
    private Navigator navigator;
    private MessageNotificationController notificationController;

    private static WhisperApplication instance;

    public static synchronized WhisperApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        appState = new AppState();
        navigator = new Navigator();
        LocalUserRepository localUserRepository = new LocalUserRepository(getApplicationContext());
        userProvider = new AppUserProvider(localUserRepository);
        userService = new HerokuUserService(getApplicationContext());
        serviceBinder = new SocketServiceBinder(getApplicationContext());
        notificationController = new MessageNotificationController(userProvider, getApplicationContext());
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public UserService getUserService() {
        return userService;
    }

    public SocketServiceBinder getServiceBinder() {
        return serviceBinder;
    }

    public MessageNotificationController getNotificationController() {
        return notificationController;
    }

    public AppState getAppState() {
        return appState;
    }
}
