package com.example.nasko.whisper.presenters.main;

import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

public interface ContactsSearchPresenter extends Presenter<ContactsSearchView> {

    void onQueryEntered(String query);

    void onContactSendRequestClick(ContactViewModel contact);
}
