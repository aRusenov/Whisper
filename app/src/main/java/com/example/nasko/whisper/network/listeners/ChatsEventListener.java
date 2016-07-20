package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Chat;

import java.util.List;

public interface ChatsEventListener {

    void onContactsLoaded(List<Chat> chats);

    void onContactUpdated(Chat chat);

    void onContactAdded(Chat chat);
}
