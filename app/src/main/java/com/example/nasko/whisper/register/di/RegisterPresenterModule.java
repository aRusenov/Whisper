package com.example.nasko.whisper.register.di;

import android.content.Context;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.register.RegisterContract;
import com.example.nasko.whisper.register.RegisterPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RegisterPresenterModule {

    private RegisterContract.View view;

    public RegisterPresenterModule(RegisterContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    RegisterContract.Presenter providePresenter(Context context, UserService userService, UserProvider userProvider) {
        return new RegisterPresenter(view, context, userService, userProvider);
    }
}
