package com.example.nasko.whisper.presenters;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.views.contracts.ChatsActionBarView;
import com.example.nasko.whisper.views.contracts.ChatsView;

public interface ChatsPresenter {

    void onTakeViews(ChatsView chatsView, ChatsActionBarView actionBarView);

    void onChatClicked(Chat clickedChat);

    void onQueryEntered(String query);

    void onContactSendRequestClick(Contact contact);

    void onLogout();

    void onResume();

    User getCurrentUser();
}
