package com.example.nasko.whisper.presenters.login;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.AbstractPresenter;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.views.contracts.LoginView;

public class LoginPresenterImpl extends AbstractPresenter<LoginView> implements LoginPresenter {

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
        this.userService.login(username, password)
                .onSuccess(user -> goToChats(user))
                .onError(error -> view.displayError(error.getMessage()))
                .execute();
    }

    @Override
    public void onRegisterClicked(String username, String password) {
        this.userService.register(username, password)
                .onSuccess(user -> goToChats(user))
                .onError(error -> view.displayError(error.getMessage()))
                .execute();
    }

    private void goToChats(User user) {
        userProvider.setCurrentUser(user);
        localUserRepository.saveLoginData(user);
        navigator.navigateToChatsScreen(context, user);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
