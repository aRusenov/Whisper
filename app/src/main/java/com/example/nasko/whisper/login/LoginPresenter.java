package com.example.nasko.whisper.login;

import android.content.Context;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.LoginModel;
import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.utils.Navigator;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private Context context;
    private CompositeSubscription subscriptions;

    private UserService userService;
    private Navigator navigator;
    private UserProvider userProvider;

    public LoginPresenter(LoginContract.View view, Context context,
                          UserService userService, Navigator navigator,
                          UserProvider userProvider) {
        this.view = view;
        this.context = context.getApplicationContext();
        this.userService = userService;
        this.navigator = navigator;
        this.userProvider = userProvider;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
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
                    navigator.navigateToChatsScreen(context, user);
                }, error -> {
                    view.hideLoadingDialog();
                    view.displayError(error.getMessage());
                });

        subscriptions.add(sub);
        view.displayLoadingDialog();
    }

    @Override
    public void onRegisterClicked() {
        navigator.navigateToRegisterScreen(context);
    }
}
