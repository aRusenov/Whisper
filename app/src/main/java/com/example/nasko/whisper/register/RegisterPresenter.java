package com.example.nasko.whisper.register;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.data.rest.RegisterModel;
import com.example.nasko.whisper.register.interactors.RegisterInteractor;

import rx.android.schedulers.AndroidSchedulers;

public class RegisterPresenter extends AbstractPresenter<RegisterContract.View> implements RegisterContract.Presenter {

    private RegisterInteractor registerInteractor;

    public RegisterPresenter(RegisterContract.View view, RegisterInteractor registerInteractor) {
        super(view);
        this.registerInteractor = registerInteractor;
    }

    @Override
    public void onRegisterClick(RegisterModel registerModel) {
        subscriptions.add(registerInteractor.register(registerModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.navigateToUserChats();
                }, error -> {
                    view.displayError(error.getMessage());
                }));

    }
}
