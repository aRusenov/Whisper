package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.SocketStateListener;

public interface ConnectionService {

    void setSocketStateListener(SocketStateListener socketStateListener);

    void setAuthenticatedListener(AuthenticationListener authenticatedListener);

    void authenticate(String token);

    void clearListeners();
}
