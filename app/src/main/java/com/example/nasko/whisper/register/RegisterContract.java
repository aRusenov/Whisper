package com.example.nasko.whisper.register;

import com.example.nasko.whisper.data.rest.RegisterModel;
import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface RegisterContract {

    interface View extends BaseView<Presenter> {

        void displayError(String message);

        void navigateToUserChats();
    }

    interface Presenter extends BasePresenter<View> {

        void onRegisterClick(RegisterModel registerModel);
    }
}
