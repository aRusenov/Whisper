package com.example.nasko.whisper.network;

import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.network.listeners.ChatsEventListener;
import com.example.nasko.whisper.network.listeners.OnErrorListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;

import java.util.List;

public interface ContactsData {

    void getContacts(String token);

    void addContact(String token, String contactUsername);

    void setContactsEventListener(ChatsEventListener listener);

    void queryContacts(String contactQuery, OnSuccessListener<List<Contact>> success, OnErrorListener<Error> error);
}
