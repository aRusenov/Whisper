package com.example.nasko.whisper.presenters;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

public class PresenterCache {

    private static final String TAG = "PresenterCache";

    private SimpleArrayMap<String, Presenter> presenters;
    private static PresenterCache instance;

    private PresenterCache() { }

    public synchronized static PresenterCache instance() {
        if (instance == null) {
            instance = new PresenterCache();
        }

        return instance;
    }

    public <P extends Presenter> P getPresenter(String tag, PresenterFactory<P> factory) {
        if (presenters == null) {
            presenters = new SimpleArrayMap<>();
        }

        P presenter = null;
        try {
            presenter = (P) presenters.get(tag);
        } catch (ClassCastException e) {
            Log.w(TAG, "Duplicate Presenter " +
                    "tag identified: " + tag + ". This could " +
                    "cause issues with state.");
        }
        if (presenter == null) {
            presenter = factory.createPresenter();
            presenters.put(tag, presenter);
        }

        return presenter;
    }

    public void removePresenter(String tag) {
        if (presenters != null) {
            presenters.remove(tag);
        }
    }
}
