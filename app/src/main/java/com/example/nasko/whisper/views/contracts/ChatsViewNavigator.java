package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.User;

public interface ChatsViewNavigator {

    void setNetworkStatus(String message);

    void navigateToChatroom(Chat chat, User user);

    void navigateToLoginScreen();
}
