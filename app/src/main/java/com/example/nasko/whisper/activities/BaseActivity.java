package com.example.nasko.whisper.activities;

import android.support.v7.app.AppCompatActivity;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.AppState;

public class BaseActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        appState.onActivityResume();
    }
}
