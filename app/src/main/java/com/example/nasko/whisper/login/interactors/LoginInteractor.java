package com.example.nasko.whisper.login.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.LoginModel;

import rx.Completable;

public interface LoginInteractor extends BaseInteractor {

    Completable login(LoginModel loginModel);
}
