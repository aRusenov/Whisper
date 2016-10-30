package com.example.nasko.whisper.models.dto;

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

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public String getChatId() {
        return chatId;
    }

    public Contact getAuthor() {
        return author;
    }

    public int getSeq() {
        return seq;
    }
}
