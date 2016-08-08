package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.RealPathUtil;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.views.contracts.ProfileView;

import java.io.File;

public class ProfilePresenterImpl implements ProfilePresenter {

    private static final String TAG = ProfilePresenterImpl.class.getName();

    private UserProvider userProvider;
    private UserService userService;
    private Context context;
    private ProfileView view;

    public ProfilePresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getUserProvider());
    }

    public ProfilePresenterImpl(UserService userService, UserProvider userProvider) {
        this.userService = userService;
        this.userProvider = userProvider;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(ProfileView view, Bundle bundle) {
        this.view = view;
        view.setUserData(userProvider.getCurrentUser());
    }

    @Override
    public void onOnlyWifiToggled(boolean useOnlyWifi) {
        // TODO: Save in local repo
    }

    @Override
    public void onImagePickedFromGallery(Uri imageUri) {
        String realPath = RealPathUtil.getRealPathFromUri(context, imageUri);
        File file = new File(realPath);

        userService.editProfile(userProvider.getCurrentUser().getSessionToken(), file.getName(), file)
            .onSuccess(user -> {
                LocalUserRepository userRepository = new LocalUserRepository(context);
                userRepository.saveLoginData(user);
                userProvider.setCurrentUser(user);

                if (view != null) {
                    view.setUserData(user);
                    view.displayMessage("Profile image changed successfully");
                }
            }).onError(error -> {
                if (view != null) {
                    view.displayMessage("Upload error: " + error.getMessage());
                }
            }).execute();
    }

    @Override
    public void detachView() {
        view = null;
    }
}
