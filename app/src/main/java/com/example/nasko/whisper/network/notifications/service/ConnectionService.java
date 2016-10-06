package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionService {

    Observable onConnect();

    Observable onDisconnect();

    Observable<User> onAuthenticated();

    void authenticate(String token);
}
