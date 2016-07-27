package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Chat;

public interface ChatsViewNavigator {

    void setNetworkStatus(String message);

    void navigateToChatroom(Chat chat);

    void navigateToLoginScreen();
}
