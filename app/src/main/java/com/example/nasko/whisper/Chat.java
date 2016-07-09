package com.example.nasko.whisper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Chat {
    private String id;
    private Message lastMessage;
    private Contact otherContact;
    private List<Contact> contacts;

    public Chat(JSONObject jsonObject, Contact otherContact) throws JSONException {
        this.otherContact = otherContact;

        JSONObject lastMessage = jsonObject.getJSONObject("lastMessage");
        this.id = (String) jsonObject.get("_id");
        this.lastMessage = new Message(lastMessage, this.id);
        this.contacts = new ArrayList<>();
    }

    public Contact getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(Contact otherContact) {
        this.otherContact = otherContact;
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
