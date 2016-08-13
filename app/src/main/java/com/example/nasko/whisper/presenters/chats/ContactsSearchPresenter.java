package com.example.nasko.whisper.presenters.chats;

import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

public interface ContactsSearchPresenter extends Presenter<ContactsSearchView> {

    void onQueryEntered(String query);

    void onContactSendRequestClick(Contact contact);
}
