package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.network.notifications.SocketService;
import com.example.nasko.whisper.network.rest.HerokuUserService;
import com.example.nasko.whisper.network.rest.UserService;

public class WhisperApplication extends Application {

    private SocketService socketService;
    private UserService userService;
    private static WhisperApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        String apiEndpoint = ConfigLoader.getConfigValue(this.getApplicationContext(), "api_url");
        this.socketService = new SocketService(apiEndpoint);
        this.userService = new HerokuUserService(this.getApplicationContext());
    }

    public SocketService getSocketService() {
        return this.socketService;
    }

    public UserService getUserService() {
        return userService;
    }

    public static synchronized WhisperApplication getInstance() {
        return instance;
    }
}
