package com.example.nasko.whisper.login;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.login.interactors.LoginInteractor;
import com.example.nasko.whisper.data.rest.LoginModel;

import rx.android.schedulers.AndroidSchedulers;

public class LoginPresenter extends AbstractPresenter<LoginContract.View> implements LoginContract.Presenter {

    private LoginInteractor loginInteractor;

    public LoginPresenter(LoginContract.View view, LoginInteractor loginInteractor) {
        super(view);
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void onLoginClicked(String username, String password) {
        subscriptions.add(loginInteractor.login(new LoginModel(username, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.navigateToUserChatsScreen();
                }, error -> {
                    view.hideLoadingDialog();
                    view.displayError(error.getMessage());
                }));

        view.displayLoadingDialog();
    }

    @Override
    public void onRegisterClicked() {
        view.navigateToRegisterScreen();
    }
}
