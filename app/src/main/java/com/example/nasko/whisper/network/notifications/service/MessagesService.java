package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

public interface MessagesService {

    void setCurrentUser(User user);

    void setMessagesEventListener(MessagesEventListener listener);

    void setNewMessageEventListener(OnNewMessageListener listener);

    void loadMessages(String chatId, int offset, int limit);

    void sendMessage(String chatId, String message);

    void clearListeners();
}
