package com.example.nasko.whisper.models;

import java.util.List;

public class ContactQueryResponse {

    private String search;
    private List<Contact> contacts;

    public String getSearch() {
        return search;
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
