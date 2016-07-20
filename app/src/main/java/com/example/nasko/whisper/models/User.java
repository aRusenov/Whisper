package com.example.nasko.whisper.models;

public class User {
    private String username;
    private String uId;
    private String sessionToken;

    public User() {}

    public User(String username, String uId, String sessionToken) {
        this.username = username;
        this.uId = uId;
        this.sessionToken = sessionToken;
    }

    public String getUsername() {
        return username;
    }

    public String getUId() {
        return uId;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
