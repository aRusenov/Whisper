package com.example.nasko.whisper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class Chat {
    private String id;
    private Message lastMessage;

    public Chat(JSONObject jsonObject) throws JSONException {
        JSONObject lastMessage = jsonObject.getJSONObject("lastMessage");
        String text = lastMessage.get("text").toString();
        String name = lastMessage.get("createdBy").toString();

        this.lastMessage = new Message(new Date(), name, text);
        this.id = jsonObject.get("_id").toString();
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
