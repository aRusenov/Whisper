package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionService {

    Observable<String> onConnect();

    Observable<String> onConnecting();

    Observable<String> onDisconnect();

    Observable<User> onAuthenticated();

    void authenticate(String token);
}
