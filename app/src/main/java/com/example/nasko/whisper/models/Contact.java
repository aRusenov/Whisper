package com.example.nasko.whisper.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact {
    private String username;
    private String name;
    private String id;
    private String updatedAt;
    private String imageUrl;
    private boolean isFriend;

    public Contact(JSONObject json) throws JSONException {
        this.id = json.getString("_id");
        this.username = json.getString("username");
        this.name = json.has("name") ? json.getString("name") : this.username;
        if (json.has("image")) {
            JSONObject image = json.getJSONObject("image");
            if (image.has("url")) {
                this.imageUrl = image.getString("url");
            }
        }

        if (json.has("isFriend")) {
            this.isFriend = json.getBoolean("isFriend");
        }
    }

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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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
