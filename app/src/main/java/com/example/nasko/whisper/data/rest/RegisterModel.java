package com.example.nasko.whisper.data.rest;

public class RegisterModel {

    private String username;
    private String password;
    private String name;
    private String imageUriPath;

    public RegisterModel(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUriPath() {
        return imageUriPath;
    }

    public void setImageUriPath(String imageUriPath) {
        this.imageUriPath = imageUriPath;
    }
}
