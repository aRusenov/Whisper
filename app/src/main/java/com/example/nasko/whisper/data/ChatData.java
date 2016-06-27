package com.example.nasko.whisper.data;

public interface ChatData {

    void requestMessages(String username, String chatId, int offset, int limit);

    void sendMessage(String username, String chatId, String message);

    void setMessagesEventListener(MessagesEventListener listener);
}
