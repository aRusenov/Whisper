package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.ContactQueryResponse;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public interface ContactsService {

    Observable<Chat> onNewChat();

    Observable<Chat[]> onLoadChats();

    Observable<ContactStateChange> onUserOnline();

    Observable<ContactStateChange> onUserOffline();

    Observable<ContactQueryResponse> onContactQueryResponse();

    void loadContacts();

    void searchContacts(String query);

    void addContact(String contactId);
}
