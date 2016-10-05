package com.example.nasko.whisper.presenters.register;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.network.RetrofitErrorMapper;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.BasePresenter;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.views.contracts.RegisterView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterPresenterImpl extends BasePresenter<RegisterView> implements RegisterPresenter {

    private UserService userService;
    private Navigator navigator;
    private UserProvider userProvider;

    public RegisterPresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getUserProvider());
    }

    public RegisterPresenterImpl(UserService userService,
                              Navigator navigator,
                              UserProvider userProvider) {
        this.userService = userService;
        this.navigator = navigator;
        this.userProvider = userProvider;
    }

    @Override
    public void onRegisterClick(RegisterModel registerModel) {
        Subscription registerSub = userService.register(registerModel)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new RetrofitErrorMapper<>(context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    userProvider.setCurrentUser(user);
                    navigator.navigateToChatsScreen(context, user);
                }, error -> {
                    if (view != null) {
                        view.displayError(error.getMessage());
                    }
                });

        subscriptions.add(registerSub);
    }
}
