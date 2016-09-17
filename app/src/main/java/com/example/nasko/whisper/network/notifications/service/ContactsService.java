package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.ContactQueryResponse;
import com.example.nasko.whisper.models.ContactStateChange;

import rx.Observable;

public interface ContactsService {

    Observable<Chat> onNewChat();

    Observable<Chat[]> onLoadChats();

    Observable<Chat> onChatUpdate();

    Observable<ContactStateChange> onUserOnline();

    Observable<ContactStateChange> onUserOffline();

    Observable<ContactQueryResponse> onContactQueryResponse();

    void loadContacts();

    void searchContacts(String query);

    void addContact(String contactId);
}
