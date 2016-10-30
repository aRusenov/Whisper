package com.example.nasko.whisper.register;

import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.register.interactors.RegisterInteractor;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View view;
    private RegisterInteractor registerInteractor;
    private CompositeSubscription subscriptions;


    public RegisterPresenter(RegisterContract.View view, RegisterInteractor registerInteractor) {
        this.view = view;
        this.registerInteractor = registerInteractor;

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        subscriptions.clear();
        view = null;
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
