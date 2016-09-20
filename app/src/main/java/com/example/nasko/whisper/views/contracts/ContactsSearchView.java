package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.dto.Contact;

import java.util.List;

public interface ContactsSearchView extends View {

    void loadQueryResults(List<Contact> contacts);

    void markContactAsFriend(Contact contact);

    void displayInfoText(String text);
}
