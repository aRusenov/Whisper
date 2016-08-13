package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import rx.Observable;

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

    public HerokuConnectionService(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    public Observable onConnect() {
        return socketManager.on(EVENT_CONNECT, Object.class);
    }

    public Observable onError() {
        return socketManager.on(EVENT_CONNECT_ERROR, Object.class);
    }

    public Observable<String> onDisconnect() {
        return socketManager.on(EVENT_DISCONNECT, String.class);
    }

    public Observable<User> onAuthenticated() {
        return socketManager.on(EVENT_AUTHENTICATED, User.class);
    }

    public Observable<String> onUnauthorized() {
        return socketManager.on(EVENT_UNAUTHORIZED, String.class);
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
}
