package com.example.nasko.whisper.register.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.RegisterModel;

import rx.Completable;

public interface RegisterInteractor extends BaseInteractor {

    Completable register(RegisterModel registerModel);
}
