package com.example.nasko.whisper.register;

import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface RegisterContract {

    interface View extends BaseView<Presenter> {

        void displayError(String message);
    }

    interface Presenter extends BasePresenter {

        void onRegisterClick(RegisterModel registerModel);
    }
}
