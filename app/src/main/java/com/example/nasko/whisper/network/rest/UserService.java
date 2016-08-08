package com.example.nasko.whisper.network.rest;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.misc.Task;

import java.io.File;

public interface UserService {

    Task<User> login(String username, String password);

    Task<User> register(String username, String password);

    Task<User> editProfile(String token, String fileName, File sourceFile);
}
