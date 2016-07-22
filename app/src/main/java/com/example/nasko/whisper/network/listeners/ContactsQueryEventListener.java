package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Contact;

import java.util.List;

public interface ContactsQueryEventListener {

    void onContactsLoaded(List<Contact> chats, String query);
}
