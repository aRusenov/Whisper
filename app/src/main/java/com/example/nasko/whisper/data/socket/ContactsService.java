package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.ContactQueryResponse;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public interface ContactsService {

    Observable<Chat> onNewChat();

    Observable<Chat[]> onLoadChats();

    Observable<ContactStateChange> onContactOnline();

    Observable<ContactStateChange> onContactOffline();

    Observable<ContactQueryResponse> onContactQueryResponse();

    void loadContacts();

    void searchContacts(String query);

    void addContact(String contactId);
}
