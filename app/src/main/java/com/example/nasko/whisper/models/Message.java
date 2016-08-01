package com.example.nasko.whisper.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Message {

    @JsonProperty("_id")
    private String id;
    private Date createdAt;
    private String text;
    private String chatId;
    @JsonProperty("createdBy")
    private Contact author;
    private int seq;

    private boolean isDummy;
    private String label;

    public Contact getAuthor() {
        return author;
    }

    public Message() { }

    public Message(String text, String chatId) {
        this.text = text;
        this.chatId = chatId;
    }

    private Message(String label) {
        this.isDummy = true;
        this.label = label;
    }

    public static Message createDummy(String label) {
        return new Message(label);
    }

    public boolean isDummy() {
        return isDummy;
    }

    public String getLabel() {
        return label;
    }

    public int getSeq() {
        return seq;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
