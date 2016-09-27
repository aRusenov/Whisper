package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.content.Intent;

import com.example.nasko.whisper.activities.ChatroomActivity;
import com.example.nasko.whisper.activities.MainActivity;
import com.example.nasko.whisper.activities.LoginActivity;
import com.example.nasko.whisper.activities.ProfileActivity;
import com.example.nasko.whisper.activities.RegisterActivity;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;

public class Navigator {

    public static final String KEY_CHAT_EXTRA = "chat";
    public static final String KEY_USER_EXTRA = "user";

    public void navigateToLoginScreen(Context context) {
        Intent launchIntent = new Intent(context, LoginActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(launchIntent);
    }

    public void navigateToChatsScreen(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_USER_EXTRA, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToProfileScreen(Context context, User user) {
        Intent launchIntent = new Intent(context, ProfileActivity.class);
        launchIntent.putExtra(KEY_USER_EXTRA, user);
        context.startActivity(launchIntent);
    }

    public void navigateToChatroom(Context context, ChatViewModel chat) {
        Intent intent = new Intent(context, ChatroomActivity.class);
        intent.putExtra(KEY_CHAT_EXTRA, chat);

        context.startActivity(intent);
    }

    public void navigateToRegisterScreen(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}
