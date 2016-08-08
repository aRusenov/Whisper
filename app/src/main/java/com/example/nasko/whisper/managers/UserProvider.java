package com.example.nasko.whisper.managers;

import com.example.nasko.whisper.models.User;

public interface UserProvider {

    User getCurrentUser();

    void setCurrentUser(User user);
}
