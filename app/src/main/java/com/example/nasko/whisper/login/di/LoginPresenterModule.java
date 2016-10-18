package com.example.nasko.whisper.login.di;

import android.content.Context;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.login.LoginContract;
import com.example.nasko.whisper.login.LoginPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginPresenterModule {

    private LoginContract.View view;

    public LoginPresenterModule(LoginContract.View view) {
        this.view = view;
    }

    @Provides @ActivityScope LoginContract.Presenter providePresenter(Context context, UserService userService, UserProvider userProvider) {
        return new LoginPresenter(view, context, userService, userProvider);
    }
}
