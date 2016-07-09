package com.example.nasko.whisper.data;

import com.example.nasko.whisper.Contact;
import com.example.nasko.whisper.data.listeners.ChatsEventListener;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

public interface ContactsData {

    void getContacts(String token);

    void addContact(String token, String contactUsername);

    void setContactsEventListener(ChatsEventListener listener);

    void queryContacts(String contactQuery, OnSuccessListener<Contact[]> success, OnErrorListener<Error> error);
}
