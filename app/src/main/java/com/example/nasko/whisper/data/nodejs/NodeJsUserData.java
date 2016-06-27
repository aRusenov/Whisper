package com.example.nasko.whisper.data.nodejs;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.OnAuthenticatedListener;
import com.example.nasko.whisper.data.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsUserData implements UserData {

    private Socket socket;
    private OnAuthenticatedListener listener;
    private User currentUser;

    public NodeJsUserData(Socket socket) {
        this.socket = socket;
        this.socket.on("authenticated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    final User user = new User(data.getString("username"), data.getString("uId"));

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAuthenticated(user);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnAuthenticatedListener(OnAuthenticatedListener listener) {
        this.listener = listener;
    }

    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void login(String username, String password) {
        if (! this.socket.connected()) {
            this.socket.connect();
        }

        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("authentication", user);
    }

    @Override
    public void register(String username, String password) {

    }
}
