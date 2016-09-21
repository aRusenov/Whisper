package com.example.nasko.whisper.presenters.login;

import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.LoginView;

public interface LoginPresenter extends Presenter<LoginView> {

    void onLoginClicked(String username, String password);

    void onRegisterClicked();
}
