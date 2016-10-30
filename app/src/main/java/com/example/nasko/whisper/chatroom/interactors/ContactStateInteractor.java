package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public interface ContactStateInteractor extends BaseInteractor {

    Observable<ContactStateChange> onStateChanged();
}
