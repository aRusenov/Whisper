package com.example.nasko.whisper.network.rest;

import com.example.nasko.whisper.models.LoginModel;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.models.User;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface UserService {

    @POST("/login")
    Observable<User> login(@Body LoginModel loginModel);

    @POST("/register")
    Observable<User> register(@Body RegisterModel registerModel);

    @Multipart
    @POST("/edit")
    Observable<User> editProfile(@Part("token") RequestBody token, @Part("image") RequestBody sourceFile);
}
