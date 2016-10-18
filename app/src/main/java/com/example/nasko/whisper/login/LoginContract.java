package com.example.nasko.whisper.login;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface LoginContract {

    interface View extends BaseView<LoginPresenter> {

        void displayLoadingDialog();

        void hideLoadingDialog();

        void displayError(String message);

        void navigateToRegisterScreen();

        void navigateToUserChatsScreen();
    }

    interface Presenter extends BasePresenter {

        void onLoginClicked(String username, String password);

        void onRegisterClicked();
    }
}
