package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Chat;

public interface ChatsViewNavigator {

    void navigateToChatroom(Chat chat);

    void navigateToLoginScreen();
}
