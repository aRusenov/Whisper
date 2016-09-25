package com.example.nasko.whisper.presenters.register;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.AbstractPresenter;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.views.contracts.RegisterView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class RegisterPresenterImpl extends AbstractPresenter<RegisterView> implements RegisterPresenter {

    private UserService userService;
    private Navigator navigator;
    private UserProvider userProvider;
    private LocalUserRepository localUserRepository;

    public RegisterPresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getLocalUserRepository());
    }

    public RegisterPresenterImpl(UserService userService,
                              Navigator navigator,
                              UserProvider userProvider,
                              LocalUserRepository localUserRepository) {
        this.userService = userService;
        this.navigator = navigator;
        this.userProvider = userProvider;
        this.localUserRepository = localUserRepository;
    }

    @Override
    public void onRegisterClick(RegisterModel registerModel) {
        Subscription registerSub = userService.register(registerModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    userProvider.setCurrentUser(user);
                    localUserRepository.saveLoginData(user);
                    navigator.navigateToChatsScreen(context, user);
                }, error -> {
                    if (view != null) {
                        view.displayError(error.getMessage());
                    }
                });

        subscriptions.add(registerSub);
    }
}
