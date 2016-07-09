package com.example.nasko.whisper.data.nodejs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.Error;
import com.example.nasko.whisper.data.ProfileData;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscriber;

public class NodeJsUserData implements UserData {

    private String loginEndpoint;
    private Socket socket;
    private User currentUser;
    private RequestQueue requestQueue;

    public NodeJsUserData(Socket socket, String loginEndpoint, Context context) {
        this.socket = socket;
        this.loginEndpoint = loginEndpoint;
        this.requestQueue = Volley.newRequestQueue(context);
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
    public void logout() {
        this.socket.disconnect();
    }

    @Override
    public Observable<User> login(final String username, final String password) {

        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(final Subscriber<? super User> subscriber) {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", username);
                    loginData.put("password", password);
                } catch (JSONException e) {
                    subscriber.onError(e);
                }

                JsonObjectRequest request = new JsonObjectRequest(loginEndpoint, loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentUser = new User(response.getString("username"), response.getString("uId"), response.getString("token"));
                            subscriber.onNext(currentUser);
                            subscriber.onCompleted();
                        } catch (JSONException e) {
                            subscriber.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", error.toString());
//                        subscriber.onError(e);
                    }
                });

                requestQueue.add(request);
            }
        });
    }

    @Override
    public Observable<ProfileData> editProfile(ProfileData profileData) {
        return null;
    }

    @Override
    public void connect(String token, final OnSuccessListener<User> successListener, final OnErrorListener<Error> errorListener) {

        if (! this.socket.connected()) {
            this.openSocketConnection(token);
        }

        this.socket.on("authenticated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject response = (JSONObject) args[0];
                try {
                    currentUser = new User(response.getString("username"), response.getString("uId"), response.getString("token"));
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            successListener.onSuccess(currentUser);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.socket.on("unauthorized", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        errorListener.onError(new Error());
                    }
                });
            }
        });
    }

    @Override
    public void register(String username, String password) {

    }

    private void openSocketConnection(String token) {
        this.socket.connect();
        JSONObject user = new JSONObject();
        try {
            user.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("authentication", user);
    }
}