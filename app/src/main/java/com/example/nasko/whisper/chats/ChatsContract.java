package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.BaseView;

import java.util.List;

public interface ChatsContract {

    interface View extends BaseView<Presenter> {

        void loadChats(List<ChatViewModel> chats);

        void addChat(ChatViewModel chat);

        void clearChats();

        void updateChatLastMessage(String chatId, MessageViewModel message);

        void setChatStatus(String chatId, boolean online);
    }

    interface Presenter extends BasePresenter {

        void onChatClicked(ChatViewModel clickedChat);
    }
}
