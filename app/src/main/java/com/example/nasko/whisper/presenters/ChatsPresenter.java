package com.example.nasko.whisper.presenters;

import android.content.Context;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.example.nasko.whisper.views.contracts.ChatsViewNavigator;

public interface ChatsPresenter extends UserProvider, Presenter {

    void onTakeChatsViewNavigator(ChatsViewNavigator chatsViewNavigator);

    void onTakeChatsView(ChatsView chatsView);

    void onTakeContactsSearchView(ContactsSearchView actionBarView);

    void onChatClicked(Chat clickedChat);

    void onQueryEntered(String query);

    void onContactSendRequestClick(Contact contact);

    void onLogout();

    void onCreate();

    void onResume();

    void onDestroy();

    User getCurrentUser();

    void setContext(Context context);
}
