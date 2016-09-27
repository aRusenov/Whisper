package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.presenters.chats.NavBarPresenter;

public interface ChatsNavBarView extends View<NavBarPresenter> {

    void setNetworkStatus(String message);
}
