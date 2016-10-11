package com.example.nasko.whisper.data.socket.consumer;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class AppStateChecker {

    private static final int PAUSE_TIMEOUT = 3;

    private boolean currentlyPaused;
    private PublishSubject<Void> pauseSubject;

    public AppStateChecker() {
        pauseSubject = PublishSubject.create();
    }

    public Observable<Void> onAppInBackground() {
        return pauseSubject.subscribeOn(Schedulers.io())
                .debounce(PAUSE_TIMEOUT, TimeUnit.SECONDS)
                .filter($ -> currentlyPaused);
    }

    public void onPause() {
        currentlyPaused = true;
        pauseSubject.onNext(null);
    }

    public void onResume() {
        currentlyPaused = false;
    }
}
