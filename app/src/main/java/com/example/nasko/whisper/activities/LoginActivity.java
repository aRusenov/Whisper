package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.network.UserData;

public class LoginActivity extends AppCompatActivity {

    private UserData userData;
    private LocalUserRepository localUserRepository;

    private Button regButton;
    private Button loginButton;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.localUserRepository = new LocalUserRepository(this);
        this.userData = WhisperApplication.getInstance().getChatService().getUserData();

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

        this.userData.login(username, password)
                .onSuccess(user -> tryConnect(user))
                .onError(error -> displayToast(error.getMessage()))
                .execute();
    }

    private void register(final String username, String password) {

        this.userData.register(username, password)
                .onSuccess(user -> tryConnect(user))
                .onError(error -> displayToast(error.getMessage()))
                .execute();
    }

    private void tryConnect(User user) {
        localUserRepository.saveLoginData(user);
        userData.connect(user.getSessionToken(),
                result -> {
                    goToContacts();
                },
                error ->  {
                    displayToast(error.getMessage());
                });
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
