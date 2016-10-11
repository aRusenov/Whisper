package com.example.nasko.whisper.editprofile;

import android.net.Uri;

import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface ProfileContract {

    interface View extends BaseView<Presenter> {

        void setUserData(User user);

        void displayMessage(String message);
    }

    interface Presenter extends BasePresenter {

//        void onOnlyWifiToggled(boolean useOnlyWifi);
//
        void onImagePickedFromGallery(Uri imageUri);
    }
}
