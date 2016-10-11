package com.example.nasko.whisper.utils;

import android.content.Context;
import android.content.Intent;

import com.example.nasko.whisper.chatroom.ChatroomActivity;
import com.example.nasko.whisper.chats.MainActivity;
import com.example.nasko.whisper.login.LoginActivity;
import com.example.nasko.whisper.editprofile.ProfileActivity;
import com.example.nasko.whisper.register.RegisterActivity;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;

public class Navigator {

    public static final String EXTRA_CHAT = "chat";
    public static final String EXTRA_USER = "user";

    public void navigateToLoginScreen(Context context) {
        Intent launchIntent = new Intent(context, LoginActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(launchIntent);
    }

    public void navigateToChatsScreen(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_USER, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToProfileScreen(Context context, User user) {
        Intent launchIntent = new Intent(context, ProfileActivity.class);
        launchIntent.putExtra(EXTRA_USER, user);
        context.startActivity(launchIntent);
    }

    public void navigateToChatroom(Context context, ChatViewModel chat) {
        Intent intent = new Intent(context, ChatroomActivity.class);
        intent.putExtra(EXTRA_CHAT, chat);

        context.startActivity(intent);
    }

    public void navigateToRegisterScreen(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}
