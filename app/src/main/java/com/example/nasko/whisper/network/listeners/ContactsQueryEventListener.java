package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.ContactQueryResponse;

public interface ContactsQueryEventListener {

    void onContactsLoaded(ContactQueryResponse response);
}
