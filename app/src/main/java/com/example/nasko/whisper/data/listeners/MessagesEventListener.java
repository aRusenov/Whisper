package com.example.nasko.whisper.data.listeners;

import com.example.nasko.whisper.Message;

import java.util.List;

public interface MessagesEventListener {

    void onMessageAdded(Message message);

    void onMessagesLoaded(List<Message> messages);
}
