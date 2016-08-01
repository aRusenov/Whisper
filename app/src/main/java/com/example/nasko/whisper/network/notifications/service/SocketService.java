package com.example.nasko.whisper.network.notifications.service;

import android.util.Log;

import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.SocketStateListener;
import com.example.nasko.whisper.network.misc.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketService {

    private static final String TAG = "SocketService";

    private User currentUser;
    private String token;
    private Socket socket;
    private boolean isAuthenticated;

    private SocketStateListener socketStateListener;
    private AuthenticationListener authenticatedListener;
    private ContactsService contactsService;
    private MessagesService messagesService;

    private JsonDeserializer deserializer;

    public SocketService(String endpoint) {
        try {
            this.socket = IO.socket(endpoint);
        } catch (URISyntaxException ex) {
            Log.d(TAG, ex.getMessage());
        }

        deserializer = new JsonDeserializer();
        contactsService = new HerokuContactsService(socket, deserializer);
        messagesService = new HerokuMessagesService(socket, deserializer);
        this.registerSocketStateListeners();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    private void registerSocketStateListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "Socket connected");
            if (token != null) {
                authenticate(token);
            }
//            if (socketStateListener != null) {
//                new Handler(Looper.getMainLooper()).post(() ->
//                        socketStateListener.onConnect());
//            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            Log.d(TAG, "Socket timeout");
//            if (socketStateListener != null) {
//                new Handler(Looper.getMainLooper()).post(() ->
//                    socketStateListener.onConnectionTimeout());
//            }
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.d(TAG, "Socket error");
//            if (socketStateListener != null) {
//                new Handler(Looper.getMainLooper()).post(() ->
//                    socketStateListener.onConnectionError());
//            }
        }).on(Socket.EVENT_DISCONNECT, args -> {
            setCurrentUser(null);
            Log.d(TAG, "Socket disconnected");
//            if (socketStateListener != null) {
//                new Handler(Looper.getMainLooper()).post(() ->
//                    socketStateListener.onDisconnect());
//            }
        });

        this.socket.on("authenticated", args -> {
            Log.d(TAG, "Socket authenticated");
            JSONObject response = (JSONObject) args[0];
            try {
                User user = new User(
                        response.getString("username"),
                        response.getString("uId"),
                        response.getString("token"));

                setCurrentUser(user);
                isAuthenticated = true;
                if (authenticatedListener != null) {
                    authenticatedListener.onAuthenticated(user);
                }
            } catch (JSONException e) {
                isAuthenticated = false;
                if (authenticatedListener != null) {
                    authenticatedListener.onUnauthorized(new Error(e.getMessage()));
                }
            }
        });

        this.socket.on("unauthorized", args -> {
            Log.d(TAG, "Unauthorized");
//            setCurrentUser(null);
            isAuthenticated = false;
            if (authenticatedListener != null) {
                authenticatedListener.onUnauthorized(new Error("Invalid session token"));
            }
        });
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

    public AuthenticationListener getAuthenticatedListener() {
        return authenticatedListener;
    }

    public void setAuthenticatedListener(AuthenticationListener authenticatedListener) {
        this.authenticatedListener = authenticatedListener;
    }

    public ContactsService getContactsService() {
        return contactsService;
    }

    public MessagesService getMessagesService() {
        return messagesService;
    }

    public void connect() {
        socket.connect();
    }

    public void reconnect() {
        socket.disconnect();
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
        }
    }

    public void clearMessagesService() {
        if (messagesService != null) {
            messagesService.clearListeners();
        }
    }

    public void dispose() {
        clearMessagesService();
        clearContactsService();
        disconnect();
    }
}