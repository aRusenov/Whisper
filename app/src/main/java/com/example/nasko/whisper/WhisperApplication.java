package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.network.ChatService;
import com.example.nasko.whisper.network.impl.NodeJsService;

public class WhisperApplication extends Application {

    public static final String SERVICE_ENDPOINT = "https://whisper-chat.herokuapp.com";
    private ChatService chatService;
    private static WhisperApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.chatService = NodeJsService.getInstance(this.getApplicationContext());
    }

    public ChatService getChatService() {
        return this.chatService;
    }

    public static synchronized WhisperApplication getInstance() {
        return instance;
    }
}
