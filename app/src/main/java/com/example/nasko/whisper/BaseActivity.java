package com.example.nasko.whisper;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {

    @Inject AppState appState;

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
