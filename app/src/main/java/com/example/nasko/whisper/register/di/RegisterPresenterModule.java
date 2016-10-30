package com.example.nasko.whisper.register.di;

import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.register.RegisterContract;
import com.example.nasko.whisper.register.RegisterPresenter;
import com.example.nasko.whisper.register.interactors.RegisterInteractor;
import com.example.nasko.whisper.register.interactors.RegisterInteractorImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class RegisterPresenterModule {

    private RegisterContract.View view;

    public RegisterPresenterModule(RegisterContract.View view) {
        this.view = view;
    }

    @Provides @ActivityScope RegisterInteractor provideRegisterInteractor(UserService userService,
                                              UserProvider userProvider,
                                              RetrofitErrorMapper<User> errorMapper) {
        return new RegisterInteractorImpl(userService, userProvider, errorMapper);
    }

    @Provides
    @ActivityScope
    RegisterContract.Presenter providePresenter(RegisterInteractor registerInteractor) {
        return new RegisterPresenter(view, registerInteractor);
    }
}
