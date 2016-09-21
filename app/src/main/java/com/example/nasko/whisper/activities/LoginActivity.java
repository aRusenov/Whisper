package com.example.nasko.whisper.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.presenters.login.LoginPresenter;
import com.example.nasko.whisper.presenters.login.LoginPresenterImpl;
import com.example.nasko.whisper.views.contracts.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements LoginView {

    private static final String TAG = LoginActivity.class.getName();

    private LoginPresenter presenter;
    private ProgressDialog dialog;

    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.tv_register) TextView tvRegister;
    @BindView(R.id.edit_username) EditText editEmail;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.error_container) LinearLayout errorContainer;
    @BindView(R.id.tv_error_msg) TextView tvErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        errorContainer.setVisibility(View.INVISIBLE);

        editPassword.setTypeface(Typeface.DEFAULT);
        editPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            presenter.onLoginClicked(username, password);

            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setMessage(getString(R.string.message_sign_in_loading));
            dialog.show();
            btnLogin.setEnabled(false);
            errorContainer.setVisibility(View.INVISIBLE);
        });

        tvRegister.setOnClickListener(v -> {

        });

        presenter = new LoginPresenterImpl();
        presenter.attachView(this, this, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public void displayError(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }

        errorContainer.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(message);
        btnLogin.setEnabled(true);
    }
}
