package com.example.nasko.whisper.network.notifications.consumer;

import com.example.nasko.whisper.network.notifications.service.SocketService;

public interface OnServiceBoundListener {

    void onServiceBind(SocketService service);

    void onServiceUnbind();
}
