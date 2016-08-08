package com.example.nasko.whisper.network.notifications.service;

import android.util.Log;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.ContactQueryResponse;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class HerokuContactsService implements ContactsService {

    public static final String EVENT_NEW_CONTACT = "new contact";
    public static final String EVENT_SHOW_CHATS = "show chats";
    public static final String EVENT_CONTACT_UPDATE = "contact update";
    public static final String EVENT_QUERY_CONTACTS = "query contacts";
    public static final String EMIT_SHOW_CHATS = "show chats";
    public static final String EMIT_QUERY_CONTACTS = "query contacts";
    public static final String EMIT_ADD_CONTACT = "add contact";

    private static final String TAG = HerokuContactsService.class.getName();

    private SocketManager socketManager;
    private ContactsEventListener contactsEventListener;
    private ContactsQueryEventListener contactsQueryEventListener;

    public HerokuContactsService(SocketManager socketManager) {
        this.socketManager = socketManager;
        register();
    }

    private void register() {
        socketManager.on(EVENT_NEW_CONTACT, Chat.class, chat -> {
            Log.d(TAG, "New contact");
            if (contactsEventListener != null) {
                contactsEventListener.onContactAdded(chat);
            }
        }).on(EVENT_SHOW_CHATS, Chat[].class, chats -> {
            Log.d(TAG, "Loading chats");
            List<Chat> chatsList = Arrays.asList(chats);

            if (contactsEventListener != null) {
                contactsEventListener.onContactsLoaded(chatsList);
            }
        }).on(EVENT_CONTACT_UPDATE, Chat.class, chat -> {
            Log.d(TAG, "Contact updated");
            if (contactsEventListener != null) {
                contactsEventListener.onContactUpdated(chat);
            }
        }).on(EVENT_QUERY_CONTACTS, ContactQueryResponse.class, response -> {
            Log.d(TAG, "Contact query returned results");
            if (contactsQueryEventListener != null) {
                contactsQueryEventListener.onContactsLoaded(response);
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
        socketManager.emit(EMIT_SHOW_CHATS);
    }

    @Override
    public void searchContacts(String query) {
        JSONObject data = new JSONObject();
        try {
            data.put("search", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_QUERY_CONTACTS, data);
    }

    @Override
    public void addContact(String contactId) {
        JSONObject data = new JSONObject();
        try {
            data.put("contact", contactId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_ADD_CONTACT, data);
    }

    @Override
    public void clearListeners() {
        contactsEventListener = null;
        contactsQueryEventListener = null;
    }
}
