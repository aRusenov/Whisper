package com.example.nasko.whisper.network.notifications;

import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;

public interface ContactsService {

    void setContactsEventListener(ContactsEventListener listener);

    void setContactsQueryEventListener(ContactsQueryEventListener listener);

    void loadContacts();

    void searchContacts(String query);

    void addContact(String contactId);

    void clearListeners();
}
