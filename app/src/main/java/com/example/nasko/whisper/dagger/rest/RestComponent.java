package com.example.nasko.whisper.dagger.rest;

import com.example.nasko.whisper.editprofile.di.EditProfilePresenterModule;
import com.example.nasko.whisper.editprofile.di.EditProfileViewComponent;
import com.example.nasko.whisper.login.di.LoginPresenterModule;
import com.example.nasko.whisper.login.di.LoginViewComponent;
import com.example.nasko.whisper.register.di.RegisterPresenterModule;
import com.example.nasko.whisper.register.di.RegisterViewComponent;

import dagger.Subcomponent;

@RestScope
@Subcomponent(modules = RestModule.class)
public interface RestComponent {

    EditProfileViewComponent plus(EditProfilePresenterModule module);

    LoginViewComponent plus(LoginPresenterModule module);

    RegisterViewComponent plus(RegisterPresenterModule module);
}
