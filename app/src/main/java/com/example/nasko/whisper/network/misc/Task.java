package com.example.nasko.whisper.network.misc;

import com.example.nasko.whisper.models.Error;

public abstract class Task<R> extends GenericTask<R, Error> {

    public Task(boolean executeOnUiThread) {
        super(executeOnUiThread);
    }
}
