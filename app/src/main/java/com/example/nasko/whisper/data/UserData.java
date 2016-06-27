package com.example.nasko.whisper.data;

import com.example.nasko.whisper.User;

import io.socket.emitter.Emitter;

public interface UserData {

    void login(String username, String password);

    void register(String username, String password);

    void setOnAuthenticatedListener(OnAuthenticatedListener listener);

    User getCurrentUser();

    void setCurrentUser(User user);
}
