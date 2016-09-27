package com.example.nasko.whisper.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.RegisterModel;
import com.example.nasko.whisper.presenters.register.RegisterPresenter;
import com.example.nasko.whisper.presenters.register.RegisterPresenterImpl;
import com.example.nasko.whisper.views.contracts.RegisterView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterView {

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

                getPresenter().onRegisterClick(registerModel);
                tvErrorMsg.setVisibility(View.INVISIBLE);
            }
        });

        setPresenter(new RegisterPresenterImpl());
        getPresenter().attachView(this, this, null);
    }

    private RegisterModel validateFields() {
        boolean valid = true;
        String username = editUsername.getText().toString().trim();
        String password = editUsername.getText().toString().trim();
        String name = editUsername.getText().toString().trim();

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
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
