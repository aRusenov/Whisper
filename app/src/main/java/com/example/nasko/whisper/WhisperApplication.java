package com.example.nasko.whisper;

import android.app.Application;

import com.example.nasko.whisper.data.ChatService;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

public class WhisperApplication extends Application {

    public static final String SERVICE_ENDPOINT = "http://192.168.0.102:3000";
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
