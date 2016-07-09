package com.example.nasko.whisper.data.listeners;

import com.example.nasko.whisper.Message;

public interface MessagesEventListener {

    void onMessageAdded(Message message);

    void onMessagesLoaded(Message[] messages);
}
