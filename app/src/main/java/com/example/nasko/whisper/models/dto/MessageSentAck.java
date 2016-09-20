package com.example.nasko.whisper.models.dto;

import com.example.nasko.whisper.models.dto.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageSentAck {

    @JsonProperty("identifier")
    private long messageIdentifier;
    private String chatId;
    private Message message;

    public long getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(long messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
