package com.example.nasko.whisper.data;

public class RequestFailedException extends Exception {

    public RequestFailedException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
