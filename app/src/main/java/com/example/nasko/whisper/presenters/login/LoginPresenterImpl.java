package com.example.nasko.whisper.presenters.login;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.BasePresenter;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.views.contracts.LoginView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class LoginPresenterImpl extends BasePresenter<LoginView> implements LoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getName();

    private UserService userService;
    private Navigator navigator;
    private UserProvider userProvider;

    public LoginPresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getUserProvider());
    }

    public LoginPresenterImpl(UserService userService,
                              Navigator navigator,
                              UserProvider userProvider) {
        this.userService = userService;
        this.navigator = navigator;
        this.userProvider = userProvider;
    }

    @Override
    public void onLoginClicked(String username, String password) {
        Subscription sub = userService.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    userProvider.setCurrentUser(user);
                    navigator.navigateToChatsScreen(context, user);
                }, error -> {
                    if (view != null) {
                        view.displayError(error.getMessage());
                    }
                });

        subscriptions.add(sub);
    }

    @Override
    public void onRegisterClicked() {
        navigator.navigateToRegisterScreen(context);
    }
}
