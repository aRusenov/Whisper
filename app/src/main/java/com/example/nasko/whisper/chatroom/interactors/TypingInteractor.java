package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.TypingEvent;

import rx.Observable;

public interface TypingInteractor extends BaseInteractor {

    Observable<TypingEvent> onTypingStart();

    Observable<TypingEvent> onTypingEnd();

    void startTyping();

    void endTyping();
}
