package com.example.nasko.whisper.presenters.register;

import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.RegisterView;

public interface RegisterPresenter extends Presenter<RegisterView> {

    void onRegisterClick(RegisterModel registerModel);
}
