package com.example.nasko.whisper.presenters.chats;

import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

public interface ContactsSearchPresenter extends Presenter<ContactsSearchView> {

    void onQueryEntered(String query);

    void onContactSendRequestClick(Contact contact);
}
