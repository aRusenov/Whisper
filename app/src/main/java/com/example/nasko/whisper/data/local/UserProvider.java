package com.example.nasko.whisper.data.local;

import com.example.nasko.whisper.models.User;

public interface UserProvider {

    User getCurrentUser();

    void setCurrentUser(User user);

    void logout();
}
