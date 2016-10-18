package com.example.nasko.whisper.login.di;

import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.login.LoginActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = LoginPresenterModule.class)
public interface LoginViewComponent {

    void inject(LoginActivity activity);
}
