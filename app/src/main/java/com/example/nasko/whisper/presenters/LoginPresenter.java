package com.example.nasko.whisper.presenters;

import android.content.Context;

import com.example.nasko.whisper.views.contracts.LoginView;

public interface LoginPresenter extends Presenter {

    void onLoginClicked(String username, String password);

    void onRegisterClicked(String username, String password);

    void attachView(LoginView loginActivity);

    void setContext(Context context);

    void detachView();
}
