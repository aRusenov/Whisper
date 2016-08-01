package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;

public interface AuthenticationListener {

    void onAuthenticated(User user);

    void onUnauthorized(Error error);
}