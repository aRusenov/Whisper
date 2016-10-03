package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.presenters.main.NavBarPresenter;

public interface ChatsNavBarView extends View<NavBarPresenter> {

    void setNetworkStatus(String message);
}
