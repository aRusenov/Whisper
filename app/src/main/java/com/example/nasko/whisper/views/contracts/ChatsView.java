package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Chat;

import java.util.List;

public interface ChatsView extends View {

    void loadChats(List<Chat> chats);

    void addChat(Chat chat);

    void updateChat(Chat chat);

    void clearChats();

    void setChatStatus(String chatId, boolean online);
}
