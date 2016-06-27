package com.example.nasko.whisper.data;

import com.example.nasko.whisper.Chat;

public abstract class ContactsEventListener {

    public abstract void onContactUpdated(Chat chat);

    public abstract void onContactAdded(Chat chat);
}
