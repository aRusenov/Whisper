package com.example.nasko.whisper.network.notifications;

import android.util.Log;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class HerokuContactsService implements ContactsService {

    private static final String TAG = "ContactsService";

    private Socket socket;
    private User currentUser;
    private ContactsEventListener contactsEventListener;
    private ContactsQueryEventListener contactsQueryEventListener;

    public HerokuContactsService(Socket socket) {
        this.socket = socket;
        this.registerEventListeners();
    }

    private void registerEventListeners() {
        socket.on("new contact", args -> {
            Log.d(TAG, "New contact");
            JSONObject rootObj = (JSONObject) args[0];
            try {
                JSONObject chatJson = rootObj.getJSONObject("chat");
                JSONObject contactJson = rootObj.getJSONArray("participants").getJSONObject(0);
                Contact contact = new Contact(contactJson);
                Chat chat = new Chat(chatJson, contact);
                if (contactsEventListener != null) {
                    contactsEventListener.onContactAdded(chat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).on("show chats", args -> {
            Log.d(TAG, "Loading chats");
            String myId = currentUser.getUId();
            JSONObject rootObj = (JSONObject) args[0];
            try {
                final Map<String, Contact> contactsData = new HashMap<>();
                JSONArray participants = rootObj.getJSONArray("participants");
                for (int i = 0; i < participants.length(); i++) {
                    Contact contact = new Contact(participants.getJSONObject(i));
                    contactsData.put(contact.getId(), contact);
                }

                JSONArray chatsJson = rootObj.getJSONArray("chats");
                final List<Chat> chats = new ArrayList<>(chatsJson.length());
                for (int i = 0; i < chatsJson.length(); i++) {
                    JSONObject json = (JSONObject) chatsJson.get(i);
                    JSONArray chatParticipants = json.getJSONArray("participants");
                    String participantId = chatParticipants.getString(0);
                    if (participantId.equals(myId) && chatParticipants.length() > 1) {
                        participantId = chatParticipants.getString(1);
                    }

                    Contact contact = contactsData.get(participantId);
                    chats.add(new Chat(json, contact));
                }
                if (contactsEventListener != null) {
                    contactsEventListener.onContactsLoaded(chats);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).on("contact update", args -> {
            Log.d(TAG, "Contact updated");
            JSONObject json = (JSONObject) args[0];
            try {
                final Chat chat = new Chat(json);
                if (contactsEventListener != null) {
                    contactsEventListener.onContactUpdated(chat);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).on("query contacts", args -> {
            Log.d(TAG, "Contact query returned results");
            JSONObject rootObj = (JSONObject) args[0];
            try {
                String query = rootObj.getString("search");
                JSONArray contactsArr = rootObj.getJSONArray("contacts");
                final List<Contact> contacts = new ArrayList<Contact>(contactsArr.length());
                for (int i = 0; i < contactsArr.length(); i++) {
                    Contact contact = new Contact(contactsArr.getJSONObject(i));
                    contacts.add(contact);
                }
                if (contactsQueryEventListener != null) {
                    contactsQueryEventListener.onContactsLoaded(contacts, query);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON");
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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
