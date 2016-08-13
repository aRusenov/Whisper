package com.example.nasko.whisper.presenters.profile;

import android.net.Uri;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.RealPathUtil;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.network.rest.UserService;
import com.example.nasko.whisper.presenters.AbstractPresenter;
import com.example.nasko.whisper.views.contracts.ProfileView;

import java.io.File;

public class ProfilePresenterImpl extends AbstractPresenter<ProfileView> implements ProfilePresenter {

    private static final String TAG = ProfilePresenterImpl.class.getName();

    private UserProvider userProvider;
    private UserService userService;

    public ProfilePresenterImpl() {
        this(WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getUserProvider());
    }

    public ProfilePresenterImpl(UserService userService, UserProvider userProvider) {
        this.userService = userService;
        this.userProvider = userProvider;
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
}
