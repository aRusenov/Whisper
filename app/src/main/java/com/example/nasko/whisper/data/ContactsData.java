package com.example.nasko.whisper.data;

import io.socket.emitter.Emitter;

public interface ContactsData {

    void getContacts(String username);

    void addContact(String username, String contact);

    void setContactsEventListener(ContactsEventListener listener);
}
