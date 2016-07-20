package com.example.nasko.whisper.network;

import com.example.nasko.whisper.network.listeners.MessagesEventListener;

public interface ChatData {

    void requestMessages(String token, String chatId, int offset, int limit);

    void sendMessage(String token, String chatId, String message);

    void setMessagesEventListener(MessagesEventListener listener);
}
