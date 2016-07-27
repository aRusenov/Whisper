package com.example.nasko.whisper.network.notifications;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

public interface MessagesService {

    void setCurrentUser(User user);

    void setMessagesEventListener(MessagesEventListener listener);

    void loadMessages(String chatId, int offset, int limit);

    void sendMessage(String chatId, String message);

    void clearListeners();
}
