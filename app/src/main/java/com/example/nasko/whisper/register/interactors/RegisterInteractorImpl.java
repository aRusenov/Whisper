package com.example.nasko.whisper.register.interactors;

import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.models.User;

import rx.Completable;
import rx.schedulers.Schedulers;

public class RegisterInteractorImpl implements RegisterInteractor {

    private UserService userService;
    private UserProvider userProvider;
    private RetrofitErrorMapper<User> errorMapper;

    public RegisterInteractorImpl(UserService userService, UserProvider userProvider,
                                  RetrofitErrorMapper<User> errorMapper) {
        this.userService = userService;
        this.userProvider = userProvider;
        this.errorMapper = errorMapper;
    }

    @Override
    public Completable register(RegisterModel registerModel) {
        return userService.register(registerModel)
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
