package com.example.nasko.whisper.login.di;

import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.login.LoginContract;
import com.example.nasko.whisper.login.LoginPresenter;
import com.example.nasko.whisper.login.interactors.LoginInteractor;
import com.example.nasko.whisper.login.interactors.LoginInteractorImpl;
import com.example.nasko.whisper.models.User;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginPresenterModule {

    private LoginContract.View view;

    public LoginPresenterModule(LoginContract.View view) {
        this.view = view;
    }

    @Provides @ActivityScope LoginInteractor provideLoginInteractor(UserService userService,
                                                                    UserProvider userProvider,
                                                                    RetrofitErrorMapper<User> errorMapper) {
        return new LoginInteractorImpl(errorMapper, userService, userProvider);
    }

    @Provides @ActivityScope LoginContract.Presenter providePresenter(LoginInteractor loginInteractor) {
        return new LoginPresenter(view, loginInteractor);
    }
}
