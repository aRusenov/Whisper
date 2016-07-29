package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.Message;

public interface OnNewMessageListener {

    void onNewMessage(Message message);
}
