package com.example.nasko.whisper.network.rest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.Image;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.misc.JsonDeserializer;
import com.example.nasko.whisper.network.misc.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HerokuUserService implements UserService {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private final String loginEndpoint;
    private final String registerEndpoint;
    private final String editProfileEndpoint;

    private RequestQueue requestQueue;
    private OkHttpClient client;
    private JsonDeserializer deserializer;

    public HerokuUserService(Context context) {
        loginEndpoint = ConfigLoader.getConfigValue(context, "api_login");
        registerEndpoint = ConfigLoader.getConfigValue(context, "api_register");
        editProfileEndpoint = ConfigLoader.getConfigValue(context, "api_edit_profile");

        requestQueue = Volley.newRequestQueue(context);
        client = new OkHttpClient();
        deserializer = new JsonDeserializer();
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

                        String imageUrl = response.getJSONObject("image").getString("url");
                        user.setImage(new Image(imageUrl));

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
                    return;
                }

                JsonObjectRequest request = new JsonObjectRequest(registerEndpoint, loginData, response -> {
                    try {
                        User user = new User(
                                response.getString("username"),
                                response.getString("uId"),
                                response.getString("token"));

                        String imageUrl = response.getJSONObject("image").getString("url");
                        user.setImage(new Image(imageUrl));

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
    public Task<User> editProfile(String token, String fileName, File sourceFile) {
        return new Task<User>(true) {
            @Override
            public void execute() {
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("token", token)
                        .addFormDataPart("image", fileName, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                        .build();

                Request request = new Request.Builder()
                        .url(editProfileEndpoint)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getErrorListener().onError(
                                new Error(e.getMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() != 200) {
                            getErrorListener().onError(new Error(
                                    response.body().string()
                            ));
                        } else {
                            User user = deserializer.deserialize(response.body().string(), User.class);
                            String relativeUrl = user.getImage().getUrl();
                            user.getImage().setUrl(relativeUrl);

                            getSuccessListener().onSuccess(user);
                        }
                    }
                });
            }
        };
    }
}
