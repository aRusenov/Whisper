package com.example.nasko.whisper.network.rest;

public class RequestFailedException extends Exception {

    public RequestFailedException(String detailMessage) {
        super(detailMessage);
    }
}
