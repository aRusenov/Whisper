package com.example.nasko.whisper.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Chat {

    @JsonProperty("_id")
    private String id;
    private Message lastMessage;
    private Contact otherContact;
    @JsonProperty("contacts")
    private List<Contact> participants;

    public Contact getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(Contact otherContact) {
        this.otherContact = otherContact;
    }

    public List<Contact> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Contact> participants) {
        this.participants = participants;
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

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        Chat other = (Chat) o;
        return other.getId().equals(this.getId());
    }
}
