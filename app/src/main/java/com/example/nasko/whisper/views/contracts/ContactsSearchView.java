package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.presenters.chats.ContactsSearchPresenter;

import java.util.List;

public interface ContactsSearchView extends View<ContactsSearchPresenter> {

    void loadQueryResults(List<Contact> contacts);

    void markContactAsFriend(Contact contact);

    void displayInfoText(String text);
}
