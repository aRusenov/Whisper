package com.example.nasko.whisper.editprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.User;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    private static final String TAG = "ProfileActivity";
    private static final int GALLERY_PICK_IMAGE = 2;
    private static final String INTENT_TYPE_IMAGE = "image/*";
    private static final String TITLE_SELECT_PROFILE_IMAGE = "Select Profile Image";

    private ProfileContract.Presenter presenter;
    private Uri galleryPickedImageUri;

    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.tv_username_title) TextView tvUsername;
    @BindView(R.id.tv_name_title) TextView tvName;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.fab_pick_image) FloatingActionButton galleryFab;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        galleryFab.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType(INTENT_TYPE_IMAGE);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, TITLE_SELECT_PROFILE_IMAGE),
                    GALLERY_PICK_IMAGE);
        });

        presenter = new ProfilePresenter(this, this,
                WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getUserProvider());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_IMAGE && resultCode == RESULT_OK) {
            galleryPickedImageUri = data.getData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();

        if (galleryPickedImageUri != null) {
            presenter.onImagePickedFromGallery(galleryPickedImageUri);
            galleryPickedImageUri = null;
        }
    }

    @Override
    public void setUserData(User user) {
        String name = user.getName();
        if (name == null) {
            name = getString(R.string.user_name_empty);
        }

        tvName.setText(name);
        tvUsername.setText(user.getUsername());
        Picasso.with(this)
                .load(user.getImage().getUrl())
                .placeholder(R.drawable.profile)
                .into(profileImage);
    }

    @Override
    public void displayMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", v -> {
            snackbar.dismiss();
        });

        snackbar.show();
    }
}
