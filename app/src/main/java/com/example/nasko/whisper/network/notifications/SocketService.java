package com.example.nasko.whisper.network.notifications;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.OnAuthenticatedListener;
import com.example.nasko.whisper.network.listeners.SocketStateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketService {

    private static final String TAG = "SocketService";

    private User currentUser;
    private String endpoint;
    private Socket socket;
    private SocketStateListener socketStateListener;
    private OnAuthenticatedListener authenticatedListener;
    private ContactsService contactsService;
    private MessagesService messagesService;

    public SocketService(String endpoint) {
        this.endpoint = endpoint;
        try {
            this.socket = IO.socket(endpoint);
        } catch (URISyntaxException ex) {
            Log.d(TAG, ex.getMessage());
        }

        this.registerSocketStateListeners();
    }

    private void registerSocketStateListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "Socket connected");
            if (socketStateListener != null) {
                socketStateListener.onConnect();
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            Log.d(TAG, "Socket timeout");
            if (socketStateListener != null) {
                socketStateListener.onConnectionTimeout();
            }
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.d(TAG, "Socket error");
            if (socketStateListener != null) {
                socketStateListener.onConnectionError();
            }
        }).on(Socket.EVENT_DISCONNECT, args -> {
            Log.d(TAG, "Socket disconnected");
            if (socketStateListener != null) {
                socketStateListener.onDisconnect();
            }
        });

        this.socket.on("authenticated", args -> {
            Log.d(TAG, "Socket authenticated");
            JSONObject response = (JSONObject) args[0];
            try {
                User user = new User(
                        response.getString("username"),
                        response.getString("uId"),
                        response.getString("token"));

                new Handler(Looper.getMainLooper()).post(() ->
                        authenticatedListener.onAuthenticated(user));
            } catch (JSONException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        authenticatedListener.onUnauthorized(new Error(e.getMessage())));
            }
        });

        this.socket.on("unauthorized", args -> new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "Unauthorized");
            authenticatedListener.onUnauthorized(new Error("Invalid session token"));
        }));
    }

    public boolean connected() {
        return socket.connected();
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public SocketStateListener getSocketStateListener() {
        return socketStateListener;
    }

    public void setSocketStateListener(SocketStateListener socketStateListener) {
        this.socketStateListener = socketStateListener;
    }

    public OnAuthenticatedListener getAuthenticatedListener() {
        return authenticatedListener;
    }

    public void setAuthenticatedListener(OnAuthenticatedListener authenticatedListener) {
        this.authenticatedListener = authenticatedListener;
    }

    public ContactsService getContactsService() {
        if (contactsService == null) {
            contactsService = new HerokuContactsService(socket);
        }

        return contactsService;
    }

    public MessagesService getMessagesService() {
        if (messagesService == null) {
            messagesService = new HerokuMessagesService(socket);
        }

        return messagesService;
    }

    public void connect() {
        socket.connect();
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void authenticate(String token) {
        JSONObject user = new JSONObject();
        try {
            user.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("authentication", user);
    }

    public void clearContactsService() {
        if (contactsService != null) {
            contactsService.clearListeners();
            contactsService = null;
        }
    }

    public void clearMessagesService() {
        if (messagesService != null) {
            messagesService.clearListeners();
            messagesService = null;
        }
    }
}
