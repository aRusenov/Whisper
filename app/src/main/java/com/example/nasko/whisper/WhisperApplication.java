package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.notifications.SocketServiceReceiver;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;

public class WhisperApplication extends Application {

    private User currentUser;
    private UserService userService;
    private SocketServiceReceiver serviceReceiver;

    private static WhisperApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        userService = new HerokuUserService(getApplicationContext());
        serviceReceiver = new SocketServiceReceiver(getApplicationContext());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public SocketServiceReceiver getServiceReceiver() {
        return serviceReceiver;
    }

    public static synchronized WhisperApplication instance() {
        return instance;
    }
}
