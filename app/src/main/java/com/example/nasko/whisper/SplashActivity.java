package com.example.nasko.whisper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.nasko.whisper.chats.MainActivity;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.login.LoginActivity;
import com.example.nasko.whisper.models.User;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {

    @Inject UserProvider userProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WhisperApplication.baseComponent().inject(this);

        User currentUser = userProvider.getCurrentUser();
        if (currentUser == null || currentUser.getSessionToken() == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            Intent chatsIntent = new Intent(this, MainActivity.class);
            startActivity(chatsIntent);
        }

        finish();
    }
}
