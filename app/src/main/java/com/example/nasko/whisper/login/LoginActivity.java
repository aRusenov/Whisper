package com.example.nasko.whisper.login;

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
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    private LoginContract.Presenter presenter;
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
            presenter.onLoginClicked(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            presenter.onRegisterClicked();
        });

        if (savedInstanceState != null && savedInstanceState.getBoolean("loading")) {
            displayLoadingDialog();
        }

        presenter = new LoginPresenter(this,
                this,
                WhisperApplication.instance().getUserService(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getUserProvider());
    }

    @Override
    public void displayLoadingDialog() {
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.message_sign_in_loading));
        dialog.show();

        btnLogin.setEnabled(false);
        tvErrorMsg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void displayError(String message) {
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
