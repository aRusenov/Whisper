package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.network.listeners.MessagesEventListener;

public interface MessagesService {

    void setMessagesEventListener(MessagesEventListener listener);

    void loadMessages(String chatId, int offset, int limit);

    void sendMessage(String chatId, String message);

    void clearListeners();
}
