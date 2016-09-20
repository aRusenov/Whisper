package com.example.nasko.whisper.models;

public enum MessageStatus {
    PENDING(0),
    SENT(1);

    private int value;

    MessageStatus(int value) {
        this.value = value;
    }
}
