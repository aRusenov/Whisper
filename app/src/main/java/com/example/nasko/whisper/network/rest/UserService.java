package com.example.nasko.whisper.network.rest;

import com.example.nasko.whisper.network.misc.Task;
import com.example.nasko.whisper.models.ProfileData;
import com.example.nasko.whisper.models.User;

public interface UserService {

    Task<User> login(String username, String password);

    Task<User> register(String username, String password);

    Task<ProfileData> editProfile(String token, ProfileData newProfileData);
}
