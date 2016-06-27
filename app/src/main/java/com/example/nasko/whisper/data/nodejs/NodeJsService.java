package com.example.nasko.whisper.data.nodejs;

import android.util.Log;


import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.ChatService;
import com.example.nasko.whisper.data.ContactsData;
import com.example.nasko.whisper.data.UserData;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsService implements ChatService {

    private static final String SERVICE_ENDPOINT = "http://192.168.56.1:3000";
    private ChatData chatData;
    private UserData userData;
    private ContactsData contactsData;
    private Socket socket;

    private static NodeJsService instance;

    public static NodeJsService getInstance() {
        if (instance == null) {
            instance = new NodeJsService();
        }

        return instance;
    }

    private NodeJsService() {
        try {
            this.socket = IO.socket(SERVICE_ENDPOINT);

        } catch (URISyntaxException ex) {
            Log.d("EX", ex.getMessage());
        }

        this.userData = new NodeJsUserData(this.socket);
        this.contactsData = new NodeJsContactsData(this.socket);
        this.chatData = new NodeJsChatData(this.socket);

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("NEWTORK", "SUCCESS");
            }
        });
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("", "");
            }
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("", "");
            }
        });

//        this.socket.connect();
    }

    @Override
    public ChatData getChatData() {
        return this.chatData;
    }

    @Override
    public ContactsData getContactsData() {
        return this.contactsData;
    }

    @Override
    public UserData getUserData() {
        return this.userData;
    }
}
