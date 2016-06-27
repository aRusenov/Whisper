package com.example.nasko.whisper;

public class User {
    private String username;
    private String uId;

    public User() {}

    public User(String username, String uId) {
        this.username = username;
        this.uId = uId;
    }

    public String getUsername() {
        return username;
    }

    public String getUId() {
        return uId;
    }
}
