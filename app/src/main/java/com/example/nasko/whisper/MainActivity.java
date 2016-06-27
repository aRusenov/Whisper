package com.example.nasko.whisper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.nasko.whisper.contacts.ContactsActivity;
import com.example.nasko.whisper.data.ChatService;
import com.example.nasko.whisper.data.OnAuthenticatedListener;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.nodejs.NodeJsService;
import com.example.nasko.whisper.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences(
                "com.example.nasko.whisper", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);
        String password = preferences.getString("password", null);

        // Temporary until session is implemented
        if (username == null || password == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        } else {
            final UserData userData = NodeJsService.getInstance().getUserData();
            userData.setOnAuthenticatedListener(new OnAuthenticatedListener() {
                @Override
                public void onAuthenticated(User user) {
                    userData.setCurrentUser(user);
                    Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                    startActivity(intent);
                }
            });

            userData.login(username, password);
        }
    }
}
