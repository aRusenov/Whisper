package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceConsumer;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;

public class WhisperApplication extends Application {

    private User currentUser;
    private UserService userService;
    private SocketServiceConsumer serviceReceiver;

    private static WhisperApplication instance;

    public static synchronized WhisperApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        userService = new HerokuUserService(getApplicationContext());
        serviceReceiver = new SocketServiceConsumer(getApplicationContext());
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

    public SocketServiceConsumer getServiceReceiver() {
        return serviceReceiver;
    }
}
