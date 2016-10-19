package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public interface ContactsStateInteractor extends BaseInteractor {

    Observable<ContactStateChange> onUserOnline();

    Observable<ContactStateChange> onUserOffline();
}
