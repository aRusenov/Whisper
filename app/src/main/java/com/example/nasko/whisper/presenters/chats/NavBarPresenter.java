package com.example.nasko.whisper.presenters.chats;

import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ChatsNavBarView;

public interface NavBarPresenter extends Presenter<ChatsNavBarView> {

    void onSettingsClicked();

    void onLogoutClicked();
}
