package com.example.nasko.whisper.network.rest;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Image;
import com.example.nasko.whisper.network.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

public class AppUserService implements UserService {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private final String loginEndpoint;
    private final String registerEndpoint;
    private final String editProfileEndpoint;

    private RequestQueue requestQueue;
    private OkHttpClient client;
    private JsonDeserializer deserializer;
    private Context context;

    public AppUserService(Context context) {
        loginEndpoint = ConfigLoader.getConfigValue(context, "api_login");
        registerEndpoint = ConfigLoader.getConfigValue(context, "api_register");
        editProfileEndpoint = ConfigLoader.getConfigValue(context, "api_edit_profile");

        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        client = new OkHttpClient();
        deserializer = new JsonDeserializer();
    }

    @Override
    public Observable<User> login(String username, String password) {
        return Observable.create(subscriber -> {
            JSONObject loginData = new JSONObject();
            try {
                loginData.put(KEY_USERNAME, username);
                loginData.put(KEY_PASSWORD, password);
            } catch (JSONException e) {
                subscriber.onError(e);
            }

            JsonObjectRequest request = new JsonObjectRequest(loginEndpoint, loginData, response -> {
                try {
                    User user = new User(
                            response.getString("username"),
                            response.getString("uId"),
                            response.getString("token"));

                    String imageUrl = response.getJSONObject("image").getString("url");
                    user.setImage(new Image(imageUrl));

                    subscriber.onNext(user);
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }, error -> {
                String message = getErrorMessage(error);
                subscriber.onError(new RequestFailedException(message));
            });

            requestQueue.add(request);
        });
    }

    @Nullable
    private String getErrorMessage(VolleyError error) {
        String message = null;
        if (error instanceof TimeoutError) {
            message = context.getString(R.string.message_timeout);
        } else if (error instanceof NoConnectionError) {
            message = context.getString(R.string.message_no_internet);
        } else if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                message = new String(error.networkResponse.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            message = context.getString(R.string.message_generic_bad_request);
        }
        return message;
    }

    @Override
    public Observable<User> register(RegisterModel registerModel) {
        return Observable.create(subscriber -> {
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("username", registerModel.getUsername());
                loginData.put("password", registerModel.getPassword());
                loginData.put("name", registerModel.getName());
            } catch (JSONException e) {
                subscriber.onError(e);
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(registerEndpoint, loginData, response -> {
                try {
                    User user = new User(
                            response.getString("username"),
                            response.getString("uId"),
                            response.getString("token"));

                    user.setName(response.getString("name"));
                    String imageUrl = response.getJSONObject("image").getString("url");
                    user.setImage(new Image(imageUrl));

                    subscriber.onNext(user);
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    subscriber.onError(e);
                }
            }, error -> {
                String message = getErrorMessage(error);
                subscriber.onError(new RequestFailedException(message));
            });

            requestQueue.add(request);
        });
    }

    @Override
    public Observable<User> editProfile(String token, String fileName, File sourceFile) {
        return Observable.create(subscriber -> {
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
                    subscriber.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() != 200) {
                        // TODO
                        subscriber.onError(null);
                    } else {
                        User user = deserializer.deserialize(response.body().string(), User.class);
                        String relativeUrl = user.getImage().getUrl();
                        user.getImage().setUrl(relativeUrl);

                        subscriber.onNext(user);
                        subscriber.onCompleted();
                    }
                }
            });
        });
    }
}
