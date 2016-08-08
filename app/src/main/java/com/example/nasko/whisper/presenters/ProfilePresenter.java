package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.example.nasko.whisper.views.contracts.ProfileView;

public interface ProfilePresenter extends Presenter {

    void setContext(Context context);

    void attachView(ProfileView view, Bundle bundle);

    void onOnlyWifiToggled(boolean useOnlyWifi);

    void onImagePickedFromGallery(Uri imageUri);

    void detachView();
}
