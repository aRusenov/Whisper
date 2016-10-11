package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.data.local.AppUserProvider;
import com.example.nasko.whisper.utils.helpers.ConfigLoader;
import com.example.nasko.whisper.data.local.LocalUserRepository;
import com.example.nasko.whisper.data.socket.consumer.MessageNotificationController;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.utils.Navigator;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
        serviceBinder = new SocketServiceBinder(getApplicationContext());
        notificationController = new MessageNotificationController(userProvider, getApplicationContext());

        String apiUrl = ConfigLoader.getConfigValue(getApplicationContext(), "api_url");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
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
