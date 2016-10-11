package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.ContactQueryResponse;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

public class AppContactsService implements ContactsService {

    public static final String EVENT_NEW_CONTACT = "new contact";
    public static final String EVENT_SHOW_CHATS = "show chats";
    public static final String EVENT_CONTACT_UPDATE = "contact update";
    public static final String EVENT_QUERY_CONTACTS = "query contacts";

    public static final String EMIT_SHOW_CHATS = "show chats";
    public static final String EMIT_QUERY_CONTACTS = "query contacts";
    public static final String EMIT_ADD_CONTACT = "add contact";

    private static final String TAG = AppContactsService.class.getName();

    private SocketManager socketManager;

    public AppContactsService(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    public Observable<Chat> onNewChat() {
        return socketManager.on(EVENT_NEW_CONTACT, Chat.class);
    }

    public Observable<Chat[]> onLoadChats() {
        return socketManager.on(EVENT_SHOW_CHATS, Chat[].class);
    }

    public Observable<Chat> onChatUpdate() {
        return socketManager.on(EVENT_CONTACT_UPDATE, Chat.class);
    }

    public Observable<ContactStateChange> onContactOnline() {
        return socketManager.on("online", ContactStateChange.class);
    }

    public Observable<ContactStateChange> onContactOffline() {
        return socketManager.on("offline", ContactStateChange.class);
    }

    public Observable<ContactQueryResponse> onContactQueryResponse() {
        return socketManager.on(EVENT_QUERY_CONTACTS, ContactQueryResponse.class);
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
}
