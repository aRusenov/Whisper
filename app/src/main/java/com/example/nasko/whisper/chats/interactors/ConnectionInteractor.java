package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionInteractor {

    Observable onConnected();

    Observable onConnecting();

    Observable<User> onAuthenticated();
}
