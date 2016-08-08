package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.managers.ImageUrlResolver;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.presenters.PresenterFactory;
import com.example.nasko.whisper.presenters.ProfilePresenter;
import com.example.nasko.whisper.presenters.ProfilePresenterImpl;
import com.example.nasko.whisper.views.contracts.ProfileView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    private static final String TAG = ProfileActivity.class.getName();
    private static final int GALLERY_PICK_IMAGE = 2;

    private PresenterFactory<ProfilePresenter> presenterFactory = () -> new ProfilePresenterImpl();
    private ProfilePresenter presenter;

    private CircleImageView profileImage;
    private TextView tvUsername;
    private TextView tvName;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton galleryFab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadViews();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        galleryFab.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), GALLERY_PICK_IMAGE);
        });

        presenter = PresenterCache.instance().getPresenter("Profile", presenterFactory);
        presenter.setContext(this);
        Bundle extras = getIntent().getExtras();
        presenter.attachView(this, extras);
    }

    private void loadViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvName = (TextView) findViewById(R.id.tv_name);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        galleryFab = (FloatingActionButton) findViewById(R.id.fab_pick_image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            presenter.onImagePickedFromGallery(imageUri);
        }
    }

    @Override
    public void setUserData(User user) {
        String name = user.getName();
        if (name == null) {
            name = "N/A";
        }

        tvName.setText(name);
        tvUsername.setText(user.getUsername());
        String imgUrl = ImageUrlResolver.getFullUrl(user.getImage());
        Picasso.with(this)
                .load(imgUrl)
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
