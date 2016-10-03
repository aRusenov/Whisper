package com.example.nasko.whisper.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.presenters.login.LoginPresenter;
import com.example.nasko.whisper.presenters.login.LoginPresenterImpl;
import com.example.nasko.whisper.views.contracts.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    private static final String TAG = "LoginActivity";

    private ProgressDialog dialog;

    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.tv_register) TextView tvRegister;
    @BindView(R.id.edit_username) EditText editEmail;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.tv_error_msg) TextView tvErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        tvErrorMsg.setVisibility(View.INVISIBLE);

        editPassword.setTypeface(Typeface.DEFAULT);
        editPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            getPresenter().onLoginClicked(username, password);

            displayDialog();
        });

        tvRegister.setOnClickListener(v -> {
            getPresenter().onRegisterClicked();
        });

        if (savedInstanceState != null && savedInstanceState.getBoolean("loading")) {
            displayDialog();
        }

        setPresenter(new LoginPresenterImpl());
        getPresenter().attachView(this, this, null);
    }

    private void displayDialog() {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.message_sign_in_loading));
        dialog.show();
        btnLogin.setEnabled(false);
        tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayError(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }

        tvErrorMsg.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(message);
        btnLogin.setEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("loading", true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
