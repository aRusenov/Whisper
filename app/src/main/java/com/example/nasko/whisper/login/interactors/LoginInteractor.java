package com.example.nasko.whisper.login.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.data.rest.LoginModel;

import rx.Completable;

public interface LoginInteractor extends BaseInteractor {

    Completable login(LoginModel loginModel);
}
