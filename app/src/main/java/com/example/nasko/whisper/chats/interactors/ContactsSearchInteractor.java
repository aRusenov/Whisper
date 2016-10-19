package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.view.ContactViewModel;

import java.util.List;

import rx.Observable;

public interface ContactsSearchInteractor extends BaseInteractor {

    void performSearch(String query);

    Observable<List<ContactViewModel>> onSearchResponse();

    void addContact(ContactViewModel contact);

    Observable<ContactViewModel> onContactAdded();
}
