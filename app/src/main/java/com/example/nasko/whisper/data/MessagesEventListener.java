package com.example.nasko.whisper.data;

import com.example.nasko.whisper.Message;

public abstract class MessagesEventListener {

    public abstract void onMessageAdded(Message message);

    public abstract void onMessagesLoaded(Message[] messages);
}
