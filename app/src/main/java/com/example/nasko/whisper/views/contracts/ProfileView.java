package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.User;

public interface ProfileView {

    void setUserData(User user);

    void displayMessage(String message);
}
