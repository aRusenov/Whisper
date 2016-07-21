package com.example.nasko.whisper.network.rest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.network.misc.Task;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.ProfileData;
import com.example.nasko.whisper.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class HerokuUserService implements UserService {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private String loginEndpoint;
    private String registerEndpoint;
    private String editProfileEndpoint;
    private RequestQueue requestQueue;

    public HerokuUserService(Context context) {
        this.loginEndpoint = ConfigLoader.getConfigValue(context, "api_login");
        this.registerEndpoint = ConfigLoader.getConfigValue(context, "api_register");
        this.editProfileEndpoint = ConfigLoader.getConfigValue(context, "api_edit_profile");
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public Task<User> login(String username, String password) {
        return new Task<User>(true) {
            @Override
            public void execute() {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put(KEY_USERNAME, username);
                    loginData.put(KEY_PASSWORD, password);
                } catch (JSONException e) {
                    getErrorListener().onError(new Error(e.getMessage()));
                }

                JsonObjectRequest request = new JsonObjectRequest(loginEndpoint, loginData, response -> {
                    try {
                        User user = new User(
                                response.getString("username"),
                                response.getString("uId"),
                                response.getString("token"));

                        getSuccessListener().onSuccess(user);
                    } catch (JSONException e) {
                        getErrorListener().onError(new Error(e.getMessage()));
                    }
                }, error -> getErrorListener().onError(new Error(error.getMessage())));

                requestQueue.add(request);
            }
        };
    }

    @Override
    public Task<User> register(String username, String password) {
        return new Task<User>(true) {
            @Override
            public void execute() {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", username);
                    loginData.put("password", password);
                } catch (JSONException e) {
                    getErrorListener().onError(new Error(e.getMessage()));
                }

                JsonObjectRequest request = new JsonObjectRequest(registerEndpoint, loginData, response -> {
                    try {
                        User user = new User(
                                response.getString("username"),
                                response.getString("uId"),
                                response.getString("token"));

                        getSuccessListener().onSuccess(user);
                    } catch (JSONException e) {
                        getErrorListener().onError(new Error(e.getMessage()));
                    }
                }, error -> getErrorListener().onError(new Error(error.getMessage())));

                requestQueue.add(request);
            }
        };
    }

    @Override
    public Task<ProfileData> editProfile(String token, ProfileData newProfileData) {
        return null;
    }
}
