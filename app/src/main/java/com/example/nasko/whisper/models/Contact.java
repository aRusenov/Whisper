package com.example.nasko.whisper.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {
    private String username;
    private String name;
    @JsonProperty("_id")
    private String id;
    private String imageUrl;
    @JsonProperty("isFriend")
    private boolean isFriend;

    public Contact() { }

    public Contact(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        Contact other = (Contact) o;
        return this.id.equals(other.getId());
    }
}
