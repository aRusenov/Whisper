package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.presenters.main.ChatsPresenter;

import java.util.List;

public interface ChatsView extends View<ChatsPresenter> {

    void loadChats(List<ChatViewModel> chats);

    void addChat(ChatViewModel chat);

    void clearChats();

    void updateChatLastMessage(String chatId, MessageViewModel message);

    void setChatStatus(String chatId, boolean online);
}
