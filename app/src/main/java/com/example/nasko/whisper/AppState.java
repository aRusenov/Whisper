package com.example.nasko.whisper;

public class AppState {

    private boolean stopped = true;

    public void onActivityStart() {
        stopped = false;
    }

    public void onActivityStop() {
        stopped = true;
    }

    public boolean inBackground() {
        return stopped;
    }
}
