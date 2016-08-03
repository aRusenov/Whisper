package com.example.nasko.whisper.network.notifications.service;

public interface SocketEventListener<R> {

    void onResult(R result);
}
