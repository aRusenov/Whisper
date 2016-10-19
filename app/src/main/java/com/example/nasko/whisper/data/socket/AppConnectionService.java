package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import rx.Observable;

public class AppConnectionService implements ConnectionService {

    private static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    private static final String EVENT_DISCONNECT = Socket.EVENT_DISCONNECT;
    private static final String EVENT_AUTHENTICATED = "authenticated";

    private static final String EMIT_AUTHENTICATE = "authentication";

    private SocketManager socketManager;

    public AppConnectionService(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

//    public Observable<String> onSocketStateChange() {
//        return Observable.merge(socketManager.on(EVENT_CONNECT),
//                socketManager.on(EVENT_AUTHENTICATED),
//                socketManager.on(Socket.EVENT_CONNECTING),
//                socketManager.on(Socket.EVENT_CONNECT_ERROR));
//    }

    public Observable<String> onConnect() {
        return socketManager.on(EVENT_CONNECT);
    }

    public Observable<String> onConnecting() {
        return socketManager.on(Socket.EVENT_CONNECTING);
    }

    public Observable<User> onAuthenticated() {
        return socketManager.on(EVENT_AUTHENTICATED, User.class);
    }

    public Observable<String> onDisconnect() {
        return socketManager.on(EVENT_DISCONNECT);
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
