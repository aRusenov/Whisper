package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.presenters.LoginPresenter;
import com.example.nasko.whisper.presenters.LoginPresenterImpl;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.presenters.PresenterFactory;
import com.example.nasko.whisper.views.contracts.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private PresenterFactory<LoginPresenter> presenterFactory = LoginPresenterImpl::new;
    private LoginPresenter loginPresenter;

    private Button btnLogin;
    private Button btnRegister;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = PresenterCache.instance().getPresenter("Login", presenterFactory);
        loginPresenter.attachView(this);
        loginPresenter.setContext(this);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        loadViews();

        this.btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            loginPresenter.onLoginClicked(username, password);
        });

        this.btnRegister.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            loginPresenter.onRegisterClicked(username, password);
        });
    }

    private void loadViews() {
        this.btnRegister = (Button) this.findViewById(R.id.btn_reg);
        this.btnLogin = (Button) this.findViewById(R.id.btn_login);
        this.editEmail = (EditText) this.findViewById(R.id.edit_email);
        this.editPassword = (EditText) this.findViewById(R.id.edit_password);
    }

    @Override
    public void navigateToContacts() {
        Intent intent = new Intent(this, ChatsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    @Override
    public void displayError(String message) {
        Toast errorToast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
        errorToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.detachView();
    }
}
