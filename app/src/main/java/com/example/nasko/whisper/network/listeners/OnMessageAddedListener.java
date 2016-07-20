package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Message;

public abstract class OnMessageAddedListener {

    public abstract void onMessageAdded(Message message);
}
