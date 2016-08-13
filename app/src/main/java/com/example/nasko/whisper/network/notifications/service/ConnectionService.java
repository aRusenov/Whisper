package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionService {

    Observable onConnect();

    Observable onError();

    Observable<String> onDisconnect();

    Observable<User> onAuthenticated();

    Observable<String> onUnauthorized();

    void authenticate(String token);
}
