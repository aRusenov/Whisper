package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.network.listeners.OnAuthenticatedListener;
import com.example.nasko.whisper.network.notifications.SocketService;
import com.example.nasko.whisper.network.rest.UserService;

public class LoginActivity extends AppCompatActivity {

    private UserService userService;
    private SocketService socketService;
    private LocalUserRepository localUserRepository;

    private Button regButton;
    private Button loginButton;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localUserRepository = new LocalUserRepository(this);
        userService = WhisperApplication.getInstance().getUserService();
        socketService = WhisperApplication.getInstance().getSocketService();

        initUi();
        socketService.setAuthenticatedListener(new OnAuthenticatedListener() {
            @Override
            public void onAuthenticated(User user) {
                socketService.setCurrentUser(user);
                goToContacts();
            }

            @Override
            public void onUnauthorized(Error error) {
                displayToast(error.getMessage());
            }
        });
    }

    private void initUi() {
        this.regButton = (Button) this.findViewById(R.id.btn_reg);
        this.loginButton = (Button) this.findViewById(R.id.btn_login);
        this.editEmail = (EditText) this.findViewById(R.id.edit_email);
        this.editPassword = (EditText) this.findViewById(R.id.edit_password);

        // Temporary set values
        editEmail.setText("Az");
        editPassword.setText("123");

        this.loginButton.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            login(username, password);
        });

        this.regButton.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            register(username, password);
        });
    }

    private void login(final String username, String password) {

        this.userService.login(username, password)
                .onSuccess(user -> tryConnect(user))
                .onError(error -> displayToast(error.getMessage()))
                .execute();
    }

    private void register(final String username, String password) {

        this.userService.register(username, password)
                .onSuccess(user -> tryConnect(user))
                .onError(error -> displayToast(error.getMessage()))
                .execute();
    }

    private void tryConnect(User user) {
        localUserRepository.saveLoginData(user);
        socketService.connect();
        socketService.authenticate(user.getSessionToken());
    }

    private void displayToast(String message) {
        Toast errorToast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
        errorToast.show();
    }

    private void goToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
