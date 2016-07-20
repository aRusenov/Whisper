package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Message;

import java.util.List;

public interface MessagesEventListener {

    void onMessageAdded(Message message);

    void onMessagesLoaded(List<Message> messages);
}
