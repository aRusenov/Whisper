package com.example.nasko.whisper.network.notifications.service;

import android.util.Log;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.ContactQueryResponse;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;
import com.example.nasko.whisper.network.misc.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.socket.client.Socket;

public class HerokuContactsService implements ContactsService {

    private static final String TAG = "ContactsService";

    private Socket socket;
    private ContactsEventListener contactsEventListener;
    private ContactsQueryEventListener contactsQueryEventListener;
    private JsonDeserializer deserializer;

    public HerokuContactsService(Socket socket, JsonDeserializer deserializer) {
        this.deserializer = deserializer;
        this.socket = socket;
        this.registerEventListeners();
    }

    private void registerEventListeners() {
        socket.on("new contact", args -> {
            Log.d(TAG,"New contact");
            if (contactsEventListener != null) {
//                contactsEventListener.onContactAdded(chat);
            }
        }).on("show chats", args -> {
            Log.d(TAG, "Loading chats");
            String json = args[0].toString();
            try {
                List<Chat> chats = deserializer.deserializeCollection(json, List.class, Chat.class);

                if (contactsEventListener != null) {
                    contactsEventListener.onContactsLoaded(chats);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).on("contact update", args -> {
            Log.d(TAG, "Contact updated");
            String json = args[0].toString();
            try {
                Chat chat = deserializer.deserialize(json, Chat.class);
                if (contactsEventListener != null) {
                    contactsEventListener.onContactUpdated(chat);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).on("query contacts", args -> {
            Log.d(TAG, "Contact query returned results");
            String json = args[0].toString();
            try {
                ContactQueryResponse response = deserializer.deserialize(json, ContactQueryResponse.class);
                if (contactsQueryEventListener != null) {
                    contactsQueryEventListener.onContactsLoaded(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setContactsEventListener(ContactsEventListener listener) {
        this.contactsEventListener = listener;
    }

    @Override
    public void setContactsQueryEventListener(ContactsQueryEventListener listener) {
        this.contactsQueryEventListener = listener;
    }

    @Override
    public void loadContacts() {
        this.socket.emit("show chats");
    }

    @Override
    public void searchContacts(String query) {
        JSONObject data = new JSONObject();
        try {
            data.put("search", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("query contacts", data);
    }

    @Override
    public void addContact(String contactId) {
        JSONObject data = new JSONObject();
        try {
            data.put("contact", contactId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("add contact", data);
    }

    @Override
    public void clearListeners() {
        contactsEventListener = null;
        contactsQueryEventListener = null;
    }
}
