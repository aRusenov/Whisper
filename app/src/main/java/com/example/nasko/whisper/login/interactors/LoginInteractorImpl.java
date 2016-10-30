package com.example.nasko.whisper.login.interactors;

import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.models.LoginModel;
import com.example.nasko.whisper.models.User;

import rx.Completable;
import rx.schedulers.Schedulers;

public class LoginInteractorImpl implements LoginInteractor {

    private RetrofitErrorMapper<User> errorMapper;
    private UserService userService;
    private UserProvider userProvider;

    public LoginInteractorImpl(RetrofitErrorMapper<User> errorMapper,
                               UserService userService, UserProvider userProvider) {
        this.errorMapper = errorMapper;
        this.userService = userService;
        this.userProvider = userProvider;
    }

    @Override
    public Completable login(LoginModel loginModel) {
        return userService.login(loginModel)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(errorMapper)
                .doOnNext(user -> userProvider.setCurrentUser(user))
                .toCompletable();
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
