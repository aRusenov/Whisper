package com.example.nasko.whisper.login;

import com.example.nasko.whisper.login.interactors.LoginInteractor;
import com.example.nasko.whisper.models.LoginModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private LoginInteractor loginInteractor;
    private CompositeSubscription subscriptions;

    public LoginPresenter(LoginContract.View view, LoginInteractor loginInteractor) {
        this.view = view;
        this.loginInteractor = loginInteractor;

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        subscriptions.clear();
        view = null;
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
