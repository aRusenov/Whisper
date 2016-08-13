package com.example.nasko.whisper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.presenters.login.LoginPresenter;
import com.example.nasko.whisper.presenters.login.LoginPresenterImpl;
import com.example.nasko.whisper.views.contracts.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getName();

    private LoginPresenter presenter;

    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_reg) Button btnRegister;
    @BindView(R.id.edit_email) EditText editEmail;
    @BindView(R.id.edit_password) EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        this.btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            presenter.onLoginClicked(username, password);
        });

        this.btnRegister.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            presenter.onRegisterClicked(username, password);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new LoginPresenterImpl();
        presenter.attachView(this, this, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public void displayError(String message) {
        Toast errorToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        errorToast.show();
    }
}
