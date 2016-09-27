package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.presenters.profile.ProfilePresenter;

public interface ProfileView extends View<ProfilePresenter> {

    void setUserData(User user);

    void displayMessage(String message);
}
