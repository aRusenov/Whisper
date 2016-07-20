package com.example.nasko.whisper.network.impl;

import android.content.Context;
import android.util.Log;
import com.example.nasko.whisper.WhisperApplication;

import com.example.nasko.whisper.network.ChatData;
import com.example.nasko.whisper.network.ChatService;
import com.example.nasko.whisper.network.ContactsData;
import com.example.nasko.whisper.network.UserData;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsService implements ChatService {

    private ChatData chatData;
    private UserData userData;
    private ContactsData contactsData;
    private Socket socket;
    private Context context;

    private static NodeJsService instance;

    public static NodeJsService getInstance() {
        return getInstance(null);
    }

    public static NodeJsService getInstance(Context context) {
        if (instance == null) {
            instance = new NodeJsService(context);
        }

        return instance;
    }

    private NodeJsService(Context context) {
        this.context = context;

        try {
            this.socket = IO.socket(WhisperApplication.SERVICE_ENDPOINT);
        } catch (URISyntaxException ex) {
            Log.d("SOCKET", ex.getMessage());
        }

        this.userData = new NodeJsUserData(this.socket, WhisperApplication.SERVICE_ENDPOINT, this.context);
        this.contactsData = new NodeJsContactsData(this.socket);
        this.chatData = new NodeJsChatData(this.socket);

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKET", "initial socket connection");
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("SOCKET", "socket timeout");
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("SOCKET", "socket connection error");
            }
        });
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

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return this.context;
    }
}
