package com.example.nasko.whisper.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nasko.whisper.User;
import com.example.nasko.whisper.contacts.ContactsActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.data.OnAuthenticatedListener;
import com.example.nasko.whisper.data.UserData;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    private Button regButton;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.userData = NodeJsService.getInstance().getUserData();

        this.regButton = (Button) this.findViewById(R.id.btn_reg);
        this.loginButton = (Button) this.findViewById(R.id.btn_login);
        this.emailText = (EditText) this.findViewById(R.id.edit_email);
        this.passwordText = (EditText) this.findViewById(R.id.edit_password);

        // Temporary set values
        emailText.setText("Az");
        passwordText.setText("123");

        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            login(emailText.getText().toString(), passwordText.getText().toString());
            }
        });

        this.regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            register(emailText.getText().toString(), passwordText.getText().toString());
            }
        });
    }

    private void login(final String username, String password) {
        this.userData.setOnAuthenticatedListener(new OnAuthenticatedListener() {
            @Override
            public void onAuthenticated(User user) {
                userData.setCurrentUser(user);
                Log.v("USER", "logged");
                saveLoginData(user);
                goToContacts();
            }
        });

        this.userData.login(username, password);
    }

    private void register(final String email, String password) {
//        this.authService.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                Log.i("USER", "reg success");
//                saveLoginData(authResult);
//                goToContacts(email, authResult.getUser().getUid());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i("USER", "reg failure");
//            }
//        });
    }

    private void saveLoginData(User user) {
        SharedPreferences preferences = this.getSharedPreferences(
                "com.example.nasko.whisper", Context.MODE_PRIVATE);

        preferences.edit().putString("username", user.getUsername()).apply();
        preferences.edit().putString("password", passwordText.getText().toString()).apply();
        preferences.edit().putString("uId", user.getUId()).apply();
    }

    private void goToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        this.startActivity(intent);
    }
}
