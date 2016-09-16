package com.example.nasko.whisper.network.notifications.consumer;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppStateChecker {

    public interface AppBackgroundListener {
        void onAppInBackground();
    }

    private static final int PAUSE_TIMEOUT_MS = 3000;

    private Object lock = new Object();
    private boolean stopped;
    private Thread worker;
    private Date lastPaused;
    private Date lastResumed;
    private AtomicBoolean isPaused;
    private AppBackgroundListener listener;

    public AppStateChecker() {
        isPaused = new AtomicBoolean(false);
        worker = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    if (stopped) return;
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (stopped) return;

                try {
                    Thread.sleep(PAUSE_TIMEOUT_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isPaused.get()) {
                    // TODO: set additional check
                    listener.onAppInBackground();
                }
            }
        });
    }

    public void setAppInBackgroundListener(AppBackgroundListener listener) {
        this.listener = listener;
    }

    public void onPause() {
        synchronized (lock) {
            isPaused.set(true);
            lock.notify();
        }
    }

    public void onResume() {
        synchronized (lock) {
            isPaused.set(false);
        }
    }

    public void start() {
        if (!worker.isAlive()) {
            worker.start();
        }
    }

    public void stop() {
        stopped = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}
