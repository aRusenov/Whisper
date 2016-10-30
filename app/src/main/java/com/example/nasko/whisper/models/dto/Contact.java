package com.example.nasko.whisper.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {

    private String username;
    private String name;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("isFriend")
    private boolean isFriend;
    private boolean isUser;
    @JsonProperty("online")
    private boolean isOnline;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
