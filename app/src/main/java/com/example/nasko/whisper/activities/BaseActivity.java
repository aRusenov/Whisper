package com.example.nasko.whisper.activities;

import android.support.v7.app.AppCompatActivity;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.AppState;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.View;

public class BaseActivity<P extends Presenter> extends AppCompatActivity implements View<P> {

    private P presenter;
    private AppState appState;

    public BaseActivity() {
        this(WhisperApplication.instance().getAppState());
    }

    public BaseActivity(AppState appState) {
        this.appState = appState;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appState.onActivityPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appState.onActivityResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public P getPresenter() {
        return presenter;
    }
}
