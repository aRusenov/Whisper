package com.example.nasko.whisper.network.impl;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.network.ContactsData;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.network.listeners.ChatsEventListener;
import com.example.nasko.whisper.network.listeners.OnErrorListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsContactsData implements ContactsData {

    private Socket socket;
    private ChatsEventListener listener;

    public NodeJsContactsData(Socket socket) {
        this.socket = socket;
        socket.on("new contact", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // TODO: add chat
            }
        }).on("show chats", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String myId = WhisperApplication.getInstance().getChatService().getUserData().getCurrentUser().getUId();
                JSONObject rootObj = (JSONObject) args[0];
                try {
                    final Map<String, Contact> contactsData = new HashMap<>();
                    JSONArray participants = rootObj.getJSONArray("participants");
                    for (int i = 0; i < participants.length(); i++) {
                        Contact contact = new Contact(participants.getJSONObject(i));
                        contact.setImageUrl(WhisperApplication.SERVICE_ENDPOINT + "/images/" + contact.getImageUrl());
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

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onContactsLoaded(chats);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("contact update", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject json = (JSONObject) args[0];
                try {
                    final Chat chat = new Chat(json);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onContactUpdated(chat);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
//                }
                }
            }
        });
    }

    @Override
    public void getContacts(String username) {
        if (! this.socket.connected()) {
            this.socket.connect();
        }

        this.socket.emit("show chats", username);
    }

    @Override
    public void addContact(String sessionToken, String contact) {
        JSONObject data = new JSONObject();
        try {
            data.put("token", sessionToken);
            data.put("contact", contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("add contact", data);
    }

    @Override
    public void setContactsEventListener(ChatsEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void queryContacts(final String contactQuery, final OnSuccessListener<List<Contact>> success, OnErrorListener<Error> error) {

        this.socket.on("query contacts", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArr = (JSONArray) args[0];
                final List<Contact> contacts = new ArrayList<Contact>(jsonArr.length());
                try {
                    for (int i = 0; i < jsonArr.length(); i++) {
                        Contact contact = new Contact(jsonArr.getJSONObject(i));
                        contact.setImageUrl(WhisperApplication.SERVICE_ENDPOINT + "/images/" + contact.getImageUrl());
                        contacts.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        success.onSuccess(contacts);
                    }
                });
            }
        });

        JSONObject data = new JSONObject();
        try {
            data.put("search", contactQuery);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("query contacts", data);
    }
}
