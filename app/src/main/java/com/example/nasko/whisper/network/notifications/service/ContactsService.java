package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;

public interface ContactsService {

    void setCurrentUser(User user);

    void setContactsEventListener(ContactsEventListener listener);

    void setContactsQueryEventListener(ContactsQueryEventListener listener);

    void loadContacts();

    void searchContacts(String query);

    void addContact(String contactId);

    void clearListeners();
}
