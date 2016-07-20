package com.example.nasko.whisper.data;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.data.listeners.OnErrorListener;
import com.example.nasko.whisper.data.listeners.OnSuccessListener;

public abstract class Task<R, E> {

    private OnSuccessListener<R> successListener;
    private OnErrorListener<E> errorListener;
    private boolean executeOnUiThread;

    public Task(boolean executeOnUiThread) {
        this.executeOnUiThread = executeOnUiThread;
    }

    public OnSuccessListener<R> getSuccessListener() {
        return successListener;
    }

    public Task<R, E> onSuccess(final OnSuccessListener<R> successListener) {
        if (executeOnUiThread) {
            this.successListener = new OnSuccessListener<R>() {
                @Override
                public void onSuccess(final R r) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            successListener.onSuccess(r);
                        }
                    });
                }
            };
        } else {
            this.successListener = successListener;
        }

        return this;
    }

    public OnErrorListener<E> getErrorListener() {
        return errorListener;
    }

    public Task<R, E> onError(final OnErrorListener<E> errorListener) {
        if (executeOnUiThread) {
            this.errorListener = new OnErrorListener<E>() {
                @Override
                public void onError(final E e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorListener.onError(e);
                        }
                    });
                }
            };
        } else {
            this.errorListener = errorListener;
        }

        return this;
    }

    public abstract void execute();
}
