package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.User;

import rx.Observable;

public interface ConnectionInteractor extends BaseInteractor {

    Observable<String> onConnectionProblem();

    Observable<String> onConnected();

    Observable<User> onAuthenticated();
}
