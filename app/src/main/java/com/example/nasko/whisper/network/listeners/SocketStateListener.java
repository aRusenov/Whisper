package com.example.nasko.whisper.network.listeners;

public interface SocketStateListener {

    void onConnect();

    void onConnectionTimeout();

    void onConnectionError();

    void onDisconnect();
}
