package com.example.nasko.whisper.managers;

import com.example.nasko.whisper.models.User;

public class AppUserProvider implements UserProvider {

    private User user;

    @Override
    public User getCurrentUser() {
        return user;
    }

    @Override
    public void setCurrentUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        this.user = user;
    }
}
