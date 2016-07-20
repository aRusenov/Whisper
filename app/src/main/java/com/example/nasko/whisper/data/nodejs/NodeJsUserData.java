package com.example.nasko.whisper.data.nodejs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.Error;
import com.example.nasko.whisper.data.ProfileData;
import com.example.nasko.whisper.data.Task;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;

public class NodeJsUserData implements UserData {

    private String loginEndpoint;
    private String registerEndpoint;
    private Socket socket;
    private User currentUser;
    private RequestQueue requestQueue;

    public NodeJsUserData(Socket socket, String serviceEndpoint, Context context) {
        this.socket = socket;
        this.loginEndpoint = serviceEndpoint + "/login";
        this.registerEndpoint = serviceEndpoint + "/register";
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
    public Task<User, Error> login(final String username, final String password) {

        return new Task<User, Error>(true) {
            @Override
            public void execute() {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", username);
                    loginData.put("password", password);
                } catch (JSONException e) {
                    getErrorListener().onError(new Error(e.getMessage()));
                }

                JsonObjectRequest request = new JsonObjectRequest(loginEndpoint, loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentUser = new User(
                                    response.getString("username"),
                                    response.getString("uId"),
                                    response.getString("token"));

                            getSuccessListener().onSuccess(currentUser);
                        } catch (JSONException e) {
                            getErrorListener().onError(new Error(e.getMessage()));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getErrorListener().onError(new Error(error.getMessage()));
                    }
                });

                requestQueue.add(request);
            }
        };
    }

    @Override
    public Observable<ProfileData> editProfile(ProfileData profileData) {
        return null;
    }

    @Override
    public void connect(String token, final OnSuccessListener<User> successListener, final OnErrorListener<Error> errorListener) {

        if (! this.socket.connected()) {
            this.socket.connect();
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
                        errorListener.onError(new Error("Invalid session token"));
                    }
                });
            }
        });

        tryAuthenticate(token);
    }

    @Override
    public Task<User, Error> register(final String username, final String password) {
        return new Task<User, Error>(true) {
            @Override
            public void execute() {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", username);
                    loginData.put("password", password);
                } catch (JSONException e) {
                    getErrorListener().onError(new Error(e.getMessage()));
                }

                JsonObjectRequest request = new JsonObjectRequest(registerEndpoint, loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentUser = new User(
                                    response.getString("username"),
                                    response.getString("uId"),
                                    response.getString("token"));

                            getSuccessListener().onSuccess(currentUser);
                        } catch (JSONException e) {
                            getErrorListener().onError(new Error(e.getMessage()));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getErrorListener().onError(new Error(error.getMessage()));
                    }
                });

                requestQueue.add(request);
            }
        };
    }

    private void tryAuthenticate(String token) {
        JSONObject user = new JSONObject();
        try {
            user.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("authentication", user);
    }
}
