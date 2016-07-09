package com.example.nasko.whisper.data.listeners;

import com.example.nasko.whisper.Message;

public abstract class OnMessagesLoadedListener {

    public abstract void onMessagesLoaded(Message[] messages);
}
