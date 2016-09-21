package com.example.nasko.whisper.presenters.login;

import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.AbstractPresenter;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.views.contracts.LoginView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class LoginPresenterImpl extends AbstractPresenter<LoginView> implements LoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getName();

    private UserService userService;
    private Navigator navigator;
    private UserProvider userProvider;
    private LocalUserRepository localUserRepository;

    public LoginPresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getLocalUserRepository());
    }

    public LoginPresenterImpl(UserService userService,
                              Navigator navigator,
                              UserProvider userProvider,
                              LocalUserRepository localUserRepository) {
        this.userService = userService;
        this.navigator = navigator;
        this.userProvider = userProvider;
        this.localUserRepository = localUserRepository;
    }

    @Override
    public void onLoginClicked(String username, String password) {
        Subscription sub = userService.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    goToChats(user);
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

    private void goToChats(User user) {
        userProvider.setCurrentUser(user);
        localUserRepository.saveLoginData(user);
        navigator.navigateToChatsScreen(context, user);
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Detaching presenter");
    }
}
