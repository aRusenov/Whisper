package com.example.nasko.whisper.network.misc;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.network.listeners.OnErrorListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;

public abstract class GenericTask<R, E> {

    private OnSuccessListener<R> successListener;
    private OnErrorListener<E> errorListener;
    private boolean executeOnUiThread;

    public GenericTask(boolean executeOnUiThread) {
        this.executeOnUiThread = executeOnUiThread;
    }

    public OnSuccessListener<R> getSuccessListener() {
        return successListener;
    }

    public GenericTask<R, E> onSuccess(final OnSuccessListener<R> successListener) {
        if (executeOnUiThread) {
            // Wrap success listener in a UI callback
            this.successListener = result -> new Handler(Looper.getMainLooper()).post(() -> successListener.onSuccess(result));
        } else {
            this.successListener = successListener;
        }

        return this;
    }

    public OnErrorListener<E> getErrorListener() {
        return errorListener;
    }

    public GenericTask<R, E> onError(final OnErrorListener<E> errorListener) {
        if (executeOnUiThread) {
            // Wrap error listener in a UI callback
            this.errorListener = e -> new Handler(Looper.getMainLooper()).post(() -> errorListener.onError(e));
        } else {
            this.errorListener = errorListener;
        }

        return this;
    }

    public abstract void execute();
}
