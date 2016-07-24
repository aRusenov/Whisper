package com.example.nasko.whisper.presenters;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.views.contracts.ChatsActionBarView;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.example.nasko.whisper.views.contracts.ChatsViewNavigator;

public interface ChatsPresenter {

    void onTakeChatsViewNavigator(ChatsViewNavigator chatsViewNavigator);

    void onTakeChatsView(ChatsView chatsView);

    void onTakeContactsSearchView(ChatsActionBarView actionBarView);

    void onChatClicked(Chat clickedChat);

    void onQueryEntered(String query);

    void onContactSendRequestClick(Contact contact);

    void onLogout();

    void onResume();

    void onDestroy();

    User getCurrentUser();
}
