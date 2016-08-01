package com.example.nasko.whisper.models;

import java.util.List;

public class MessagesQueryResponse {

    private String chatId;
    private List<Message> messages;

    public String getChatId() {
        return chatId;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
