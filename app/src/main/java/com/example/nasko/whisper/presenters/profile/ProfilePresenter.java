package com.example.nasko.whisper.presenters.profile;

import android.net.Uri;

import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ProfileView;

public interface ProfilePresenter extends Presenter<ProfileView> {

    void onOnlyWifiToggled(boolean useOnlyWifi);

    void onImagePickedFromGallery(Uri imageUri);
}
