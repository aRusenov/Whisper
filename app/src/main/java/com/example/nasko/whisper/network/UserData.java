package com.example.nasko.whisper.network;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.ProfileData;
import com.example.nasko.whisper.managers.Task;
import com.example.nasko.whisper.network.listeners.OnErrorListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;

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
