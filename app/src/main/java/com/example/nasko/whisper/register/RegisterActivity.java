package com.example.nasko.whisper.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chats.MainActivity;
import com.example.nasko.whisper.register.di.RegisterPresenterModule;
import com.example.nasko.whisper.models.RegisterModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    @Inject RegisterContract.Presenter presenter;
    private ProgressDialog dialog;

    @BindView(R.id.tv_error_msg) TextView tvErrorMsg;
    @BindView(R.id.edit_username) EditText editUsername;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.edit_name) EditText editName;
    @BindView(R.id.btn_register) Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        editPassword.setTypeface(Typeface.DEFAULT);
        editPassword.setTransformationMethod(new PasswordTransformationMethod());
        tvErrorMsg.setVisibility(View.INVISIBLE);

        btnRegister.setOnClickListener(view -> {
            RegisterModel registerModel = validateFields();
            if (registerModel != null) {
                dialog = new ProgressDialog(this);
                dialog.setIndeterminate(true);
                dialog.setMessage(getString(R.string.message_sign_in_loading));
                dialog.show();

                presenter.onRegisterClick(registerModel);
                tvErrorMsg.setVisibility(View.INVISIBLE);
            }
        });

        WhisperApplication.restComponent()
                .plus(new RegisterPresenterModule(this))
                .inject(this);

        presenter.init();
    }

    private RegisterModel validateFields() {
        boolean valid = true;
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String name = editName.getText().toString().trim();

        if (username.length() < 5) {
            editUsername.setError("at least 5 characters");
            valid = false;
        }

        if (password.length() < 3) {
            editPassword.setError("at least 3 characters");
            valid = false;
        }

        if (name.length() < 5) {
            editName.setError("at least 5 characters");
            valid = false;
        }

        if (!valid) {
            return null;
        }

        return new RegisterModel(username, password, name);
    }

    @Override
    public void displayError(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }

        tvErrorMsg.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(message);
        btnRegister.setEnabled(true);
    }

    @Override
    public void navigateToUserChats() {
        Intent intent = MainActivity.prepareIntent(this, null);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
