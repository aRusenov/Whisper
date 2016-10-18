package com.example.nasko.whisper.editprofile.di;

import android.content.Context;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.rest.UserService;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.editprofile.ProfileContract;
import com.example.nasko.whisper.editprofile.ProfilePresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class EditProfilePresenterModule {

    private ProfileContract.View view;

    public EditProfilePresenterModule(ProfileContract.View view) {
        this.view = view;
    }

    @Provides @ActivityScope ProfileContract.Presenter presenter(Context context, UserService userService, UserProvider userProvider) {
        return new ProfilePresenter(view, context, userService, userProvider);
    }
}
