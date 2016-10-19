package com.example.nasko.whisper.login;

import android.content.Context;

import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.models.LoginModel;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private CompositeSubscription subscriptions;

    private UserService userService;
    private Context context;
    private UserProvider userProvider;

    public LoginPresenter(LoginContract.View view, Context context,
                          UserService userService, UserProvider userProvider) {
        this.view = view;
        this.context = context;
        this.userService = userService;
        this.userProvider = userProvider;

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() { }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        subscriptions.clear();
        view = null;
    }

    @Override
    public void onLoginClicked(String username, String password) {
        Subscription sub = userService.login(new LoginModel(username, password))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new RetrofitErrorMapper<>(context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    userProvider.setCurrentUser(user);
                    view.navigateToUserChatsScreen();
                }, error -> {
                    view.hideLoadingDialog();
                    view.displayError(error.getMessage());
                });

        subscriptions.add(sub);
        view.displayLoadingDialog();
    }

    @Override
    public void onRegisterClicked() {
        view.navigateToRegisterScreen();
    }
}
