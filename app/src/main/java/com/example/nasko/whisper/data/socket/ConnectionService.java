package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionService {

    Observable<Void> onConnect();

    Observable<Void> onConnecting();

    Observable<Void> onDisconnect();

    Observable<User> onAuthenticated();

    void authenticate(String token);
}
