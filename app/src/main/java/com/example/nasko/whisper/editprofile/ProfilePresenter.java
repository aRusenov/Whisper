package com.example.nasko.whisper.editprofile;

import android.content.Context;
import android.net.Uri;

import com.example.nasko.whisper.utils.helpers.RealPathUtil;
import com.example.nasko.whisper.data.local.LocalUserRepository;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.data.RetrofitErrorMapper;
import com.example.nasko.whisper.data.rest.UserService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter implements ProfileContract.Presenter {

    private static final String TAG = ProfilePresenter.class.getName();

    private ProfileContract.View view;
    private Context context;
    private UserProvider userProvider;
    private UserService userService;
    private CompositeSubscription subscriptions;

    public ProfilePresenter(ProfileContract.View view, Context context,
                            UserService userService, UserProvider userProvider) {
        this.view = view;
        this.context = context.getApplicationContext();
        this.userService = userService;
        this.userProvider = userProvider;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void start() {
        User currentUser = userProvider.getCurrentUser();
        view.setUserData(currentUser);
    }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        view = null;
        subscriptions.clear();
    }

    @Override
    public void onImagePickedFromGallery(Uri imageUri) {
        String realPath = RealPathUtil.getRealPathFromUri(context, imageUri);
        File file = new File(realPath);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), userProvider.getCurrentUser().getSessionToken());
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);

        Subscription sub = userService.editProfile(token, fileBody)
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(new RetrofitErrorMapper<>(context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    LocalUserRepository userRepository = new LocalUserRepository(context);
                    userRepository.saveLoginData(user);
                    userProvider.setCurrentUser(user);

                    view.setUserData(user);
                    view.displayMessage("Profile image changed successfully");
                }, error -> {
                    view.displayMessage("Upload error: " + error.getMessage());
                });

        subscriptions.add(sub);
    }
}
