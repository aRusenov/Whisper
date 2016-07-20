package com.example.nasko.whisper.data;

import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

import rx.Observable;

public interface UserData {

    Task<User, Error> login(String username, String password);

    Observable<ProfileData> editProfile(ProfileData profileData);

    Task<User, Error> register(String username, String password);

    void connect(String token, OnSuccessListener<User> successListener, OnErrorListener<Error> error);

    User getCurrentUser();

    void setCurrentUser(User user);

    void logout();
}
