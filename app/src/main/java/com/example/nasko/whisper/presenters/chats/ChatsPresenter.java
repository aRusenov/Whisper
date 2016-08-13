package com.example.nasko.whisper.presenters.chats;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ChatsView;

public interface ChatsPresenter extends Presenter<ChatsView> {

    void onChatClicked(Chat clickedChat);
}
