package com.example.nasko.whisper.data.nodejs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.data.ContactsData;
import com.example.nasko.whisper.data.ContactsEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsContactsData implements ContactsData {

    private Socket socket;
    private ContactsEventListener listener;

    public NodeJsContactsData(Socket socket) {
        this.socket = socket;
        socket.on("new contact", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int a = 5;
            }
        }).on("show chats", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            JSONArray chats = (JSONArray)args[0];
            for (int i = 0; i < chats.length(); i++) {
                try {
                    JSONObject json = (JSONObject) chats.get(i);
                    final Chat chat = new Chat(json);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onContactAdded(chat);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    public void addContact(String username, String contact) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("contact", contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("add contact", data);
    }

    @Override
    public void setContactsEventListener(ContactsEventListener listener) {
        this.listener = listener;
    }
}
