package com.example.nasko.whisper.data.listeners;

import com.example.nasko.whisper.Chat;

import java.util.List;

public interface ChatsEventListener {

    void onContactsLoaded(List<Chat> chats);

    void onContactUpdated(Chat chat);

    void onContactAdded(Chat chat);
}
