package com.example.nasko.whisper.network.listeners;

public interface OnSocketStateListener {

    void onConnect();

    void onConnectionTimeout();

    void onConnectionError();

    void onDisconnect();
}
