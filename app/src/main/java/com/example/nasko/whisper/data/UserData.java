package com.example.nasko.whisper.data;

import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

import rx.Observable;

public interface UserData {

    Observable<User> login(String username, String password);

    Observable<ProfileData> editProfile(ProfileData profileData);

    void connect(String token, OnSuccessListener<User> successListener, OnErrorListener<Error> error);

    void register(String username, String password);

    User getCurrentUser();

    void setCurrentUser(User user);

    void logout();
}
