package com.example.nasko.whisper;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private AppState appState;

    public BaseActivity() {
        this(WhisperApplication.instance().getAppState());
    }

    public BaseActivity(AppState appState) {
        this.appState = appState;
    }

    @Override
    protected void onStart() {
        super.onStart();
        appState.onActivityStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        appState.onActivityStop();
    }
}
