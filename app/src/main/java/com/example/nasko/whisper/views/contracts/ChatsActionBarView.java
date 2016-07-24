package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Contact;

import java.util.List;

public interface ChatsActionBarView {

    void loadQueryResults(List<Contact> contacts);

    void markContactAsFriend(Contact contact);

    void displayInfoText(String text);
}
