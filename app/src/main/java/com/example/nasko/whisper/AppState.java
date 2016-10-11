package com.example.nasko.whisper;

public class AppState {

    private boolean paused = true;

    public void onActivityResume() {
        paused = false;
    }

    public void onActivityPause() {
        paused = true;
    }

    public boolean inBackground() {
        return paused;
    }
}
