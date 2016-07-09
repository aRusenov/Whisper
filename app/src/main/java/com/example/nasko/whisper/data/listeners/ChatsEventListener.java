package com.example.nasko.whisper.data.listeners;

import com.example.nasko.whisper.Chat;

public interface ChatsEventListener {

    void onContactsLoaded(Chat[] chats);

    void onContactUpdated(Chat chat);

    void onContactAdded(Chat chat);
}
