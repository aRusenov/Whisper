package com.example.nasko.whisper.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Chat {

    @JsonProperty("_id")
    private String id;
    private Message lastMessage;
    @JsonProperty("contacts")
    private List<Contact> participants;

    public List<Contact> getParticipants() {
        return participants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message getLastMessage() {
        return lastMessage;
    }
}
