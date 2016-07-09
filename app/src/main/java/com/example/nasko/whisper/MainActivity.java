package com.example.nasko.whisper;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.nasko.whisper.contacts.ContactsActivity;
import com.example.nasko.whisper.data.Error;
import com.example.nasko.whisper.data.LocalUserRepository;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;
import com.example.nasko.whisper.data.nodejs.NodeJsService;
import com.example.nasko.whisper.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private LocalUserRepository localUserRepository;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.localUserRepository = new LocalUserRepository(this);
        this.userData = NodeJsService.getInstance().getUserData();
        User loggedUser = localUserRepository.getLoggedUser();

        if (loggedUser.getSessionToken() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            this.startActivity(intent);
        } else {
            userData.setCurrentUser(loggedUser);
            userData.connect(loggedUser.getSessionToken(), new OnSuccessListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }, new OnErrorListener<Error>() {
                @Override
                public void onError(Error error) {
                    localUserRepository.logout();
                    Toast toast = Toast.makeText(MainActivity.this, "You must relog", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }
}
