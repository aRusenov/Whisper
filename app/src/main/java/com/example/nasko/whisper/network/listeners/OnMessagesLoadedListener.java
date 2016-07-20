package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Message;

public abstract class OnMessagesLoadedListener {

    public abstract void onMessagesLoaded(Message[] messages);
}
