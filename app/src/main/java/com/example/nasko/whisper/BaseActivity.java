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
    protected void onResume() {
        super.onResume();
        appState.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        appState.onActivityPause();
    }
}
