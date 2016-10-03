package com.example.nasko.whisper.presenters.main;

import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ChatsView;

public interface ChatsPresenter extends Presenter<ChatsView> {

    void onChatClicked(ChatViewModel clickedChat);
}
