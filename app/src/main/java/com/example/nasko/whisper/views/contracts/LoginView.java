package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.presenters.login.LoginPresenter;

public interface LoginView extends View<LoginPresenter> {

    void displayError(String message);
}
