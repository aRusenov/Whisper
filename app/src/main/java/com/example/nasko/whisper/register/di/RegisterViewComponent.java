package com.example.nasko.whisper.register.di;

import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.register.RegisterActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = RegisterPresenterModule.class)
public interface RegisterViewComponent {

    void inject(RegisterActivity activity);
}
