package com.example.nasko.whisper.data.nodejs;

import android.content.Context;
import android.util.Log;
import com.example.nasko.whisper.WhisperApplication;

import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.ChatService;
import com.example.nasko.whisper.data.ContactsData;
import com.example.nasko.whisper.data.UserData;

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
            Log.d("EX", ex.getMessage());
        }

        this.userData = new NodeJsUserData(this.socket, WhisperApplication.SERVICE_ENDPOINT + "/login", this.context);
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

//        this.socket.openSocketConnection();
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
