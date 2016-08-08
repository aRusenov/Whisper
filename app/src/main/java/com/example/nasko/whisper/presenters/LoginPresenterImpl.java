package com.example.nasko.whisper.presenters;

import android.content.Context;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.views.contracts.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private Context context;
    private LoginView view;
    private UserService userService;

    public LoginPresenterImpl() {
        this(WhisperApplication.instance().getUserService());
    }

    public LoginPresenterImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onLoginClicked(String username, String password) {
        this.userService.login(username, password)
                .onSuccess(user -> goToContacts(user))
                .onError(error -> view.displayError(error.getMessage()))
                .execute();
    }

    @Override
    public void onRegisterClicked(String username, String password) {
        this.userService.register(username, password)
                .onSuccess(user -> goToContacts(user))
                .onError(error -> view.displayError(error.getMessage()))
                .execute();
    }

    private void goToContacts(User user) {
        LocalUserRepository localUserRepository = new LocalUserRepository(context);
        localUserRepository.saveLoginData(user);
        view.navigateToContacts();
    }

    @Override
    public void attachView(LoginView loginView) {
        view = loginView;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void detachView() {
        view = null;
        context = null;
    }
}
