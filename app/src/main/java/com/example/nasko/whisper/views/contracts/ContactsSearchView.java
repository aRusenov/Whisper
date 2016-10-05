package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.presenters.main.ContactsSearchPresenter;

import java.util.List;

public interface ContactsSearchView extends View<ContactsSearchPresenter> {

    void showQueryResults(List<ContactViewModel> contacts);

    void markContactAsFriend(ContactViewModel contact);

    void showLoading();

    void hideLoading();

    void displayQueryTooShortError();
}
