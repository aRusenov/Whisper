package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.OnAuthenticatedListener;
import com.example.nasko.whisper.network.notifications.SocketService;

public class MainActivity extends AppCompatActivity {

    private LocalUserRepository localUserRepository;
    private SocketService socketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localUserRepository = new LocalUserRepository(this);
        socketService = WhisperApplication.getInstance().getSocketService();
        User loggedUser = localUserRepository.getLoggedUser();

        if (loggedUser.getSessionToken() == null) {
            startActivity(LoginActivity.class, false);
        } else {
            socketService.setAuthenticatedListener(new OnAuthenticatedListener() {
                @Override
                public void onAuthenticated(User user) {
                    socketService.setCurrentUser(loggedUser);
                    startActivity(ContactsActivity.class, true);
                }

                @Override
                public void onUnauthorized(Error error) {
                    localUserRepository.logout();
                    Toast toast = Toast.makeText(MainActivity.this, "You must relog", Toast.LENGTH_SHORT);
                    toast.show();

                    startActivity(LoginActivity.class, true);
                }
            });

            socketService.connect();
            socketService.authenticate(loggedUser.getSessionToken());
        }
    }

    private void startActivity(Class componentClass, boolean clearStack) {
        Intent intent = new Intent(this, componentClass);
        if (clearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        startActivity(intent);
    }
}
