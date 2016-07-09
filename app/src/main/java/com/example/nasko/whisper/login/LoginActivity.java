package com.example.nasko.whisper.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.contacts.ContactsActivity;
import com.example.nasko.whisper.data.Error;
import com.example.nasko.whisper.data.LocalUserRepository;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private UserData userData;
    private User currentUser;
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

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                login(username, password);
            }
        });
    }

    private void login(final String username, String password) {

        this.userData.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Toast errorToast = Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                        errorToast.show();
                    }

                    @Override
                    public void onNext(User user) {
                        currentUser = user;
                        localUserRepository.saveLoginData(user);
                    }

                    @Override
                    public void onCompleted() {
                        userData.connect(currentUser.getSessionToken(), new OnSuccessListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                currentUser = user;
                                goToContacts();
                            }
                        }, new OnErrorListener<Error>() {
                            @Override
                            public void onError(Error error) {
                                throw new UnsupportedOperationException();
                            }
                        });
                    }
                });
    }

    private void goToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
