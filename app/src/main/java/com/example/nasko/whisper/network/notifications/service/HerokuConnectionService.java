package com.example.nasko.whisper.network.notifications.service;

import android.util.Log;

import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.SocketStateListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class HerokuConnectionService implements ConnectionService {

    public static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    public static final String EVENT_CONNECT_TIMEOUT = Socket.EVENT_CONNECT_TIMEOUT;
    public static final String EVENT_CONNECT_ERROR = Socket.EVENT_CONNECT_ERROR;
    public static final String EVENT_DISCONNECT = Socket.EVENT_DISCONNECT;
    public static final String EVENT_UNAUTHORIZED = "unauthorized";
    public static final String EVENT_AUTHENTICATED = "authenticated";
    public static final String EMIT_AUTHENTICATE = "authentication";

    private static final String TAG = HerokuConnectionService.class.getName();

    private SocketManager socketManager;
    private SocketStateListener socketStateListener;
    private AuthenticationListener authenticatedListener;

    public HerokuConnectionService(SocketManager socketManager) {
        this.socketManager = socketManager;
        register();
    }

    public void setSocketStateListener(SocketStateListener socketStateListener) {
        this.socketStateListener = socketStateListener;
    }

    public void setAuthenticatedListener(AuthenticationListener authenticatedListener) {
        this.authenticatedListener = authenticatedListener;
    }

    private void register() {

        socketManager.on(EVENT_CONNECT, Object.class, result -> {
            Log.d(TAG, "Socket connected");
            if (socketStateListener != null) {
                socketStateListener.onConnect();
            }
        }).on(EVENT_CONNECT_TIMEOUT, Object.class, result -> {
            Log.d(TAG, "Socket timeout");
            if (socketStateListener != null) {
                socketStateListener.onConnectionTimeout();
            }
        }).on(EVENT_CONNECT_ERROR, Object.class, args -> {
            Log.d(TAG, "Socket error");
            if (socketStateListener != null) {
                socketStateListener.onConnectionError();
            }
        }).on(EVENT_DISCONNECT, String.class, args -> {
            Log.d(TAG, "Socket disconnected");
            if (socketStateListener != null) {
                socketStateListener.onDisconnect();
            }
        });

        socketManager.on(EVENT_AUTHENTICATED, User.class, user -> {
            Log.d(TAG, "Socket authenticated");
            if (authenticatedListener != null) {
                authenticatedListener.onAuthenticated(user);
            }
        }).on(EVENT_UNAUTHORIZED, String.class, msg -> {
            Log.d(TAG, "Unauthorized");
            if (authenticatedListener != null) {
                authenticatedListener.onUnauthorized(new Error("Invalid session token"));
            }
        });
    }

    public void authenticate(String token) {
        JSONObject user = new JSONObject();
        try {
            user.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_AUTHENTICATE, user);
    }

    public void clearListeners() {
        authenticatedListener = null;
        socketStateListener = null;
    }
}
