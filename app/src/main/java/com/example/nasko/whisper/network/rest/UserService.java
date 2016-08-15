package com.example.nasko.whisper.network.rest;

import com.example.nasko.whisper.models.User;

import java.io.File;

import rx.Observable;

public interface UserService {

    Observable<User> login(String username, String password);

    Observable<User> register(String username, String password);

    Observable<User> editProfile(String token, String fileName, File sourceFile);
}
