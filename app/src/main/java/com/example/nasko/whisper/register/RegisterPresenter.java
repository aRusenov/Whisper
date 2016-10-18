package com.example.nasko.whisper.register;

import android.content.Context;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.rest.UserService;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View view;
    private Context context;

    private CompositeSubscription subscriptions;
    private UserService userService;
    private UserProvider userProvider;

    public RegisterPresenter(RegisterContract.View view, Context context,
                             UserService userService, UserProvider userProvider) {
        this.view = view;
        this.context = context.getApplicationContext();
        this.userService = userService;
        this.userProvider = userProvider;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        view = null;
    }

    @Override
    public void onRegisterClick(RegisterModel registerModel) {
        Subscription registerSub = userService.register(registerModel)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new RetrofitErrorMapper<>(context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    userProvider.setCurrentUser(user);
                    view.navigateToUserChats();
                }, error -> {
                    if (view != null) {
                        view.displayError(error.getMessage());
                    }
                });

        subscriptions.add(registerSub);
    }
}
