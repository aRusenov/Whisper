package com.example.nasko.whisper.editprofile.di;

import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.editprofile.ProfileActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = EditProfilePresenterModule.class)
public interface EditProfileViewComponent {

    void inject(ProfileActivity activity);
}
