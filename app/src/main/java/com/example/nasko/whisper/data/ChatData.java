package com.example.nasko.whisper.data;

import com.example.nasko.whisper.data.listeners.MessagesEventListener;
import com.example.nasko.whisper.data.listeners.OnMessageAddedListener;
import com.example.nasko.whisper.data.listeners.OnMessagesLoadedListener;

public interface ChatData {

    void requestMessages(String token, String chatId, int offset, int limit);

    void sendMessage(String token, String chatId, String message);

    void setMessagesEventListener(MessagesEventListener listener);
}
