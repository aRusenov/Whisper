package com.example.nasko.whisper.network.notifications.consumer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.nasko.whisper.network.notifications.service.BackgroundSocketService;
import com.example.nasko.whisper.network.notifications.service.SocketService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class SocketServiceBinder {

    private static final String TAG = SocketServiceBinder.class.getName();

    private PublishSubject<BackgroundSocketService> serviceBoundSubject = PublishSubject.create();
    private PublishSubject<Void> serviceUnboundSubject = PublishSubject.create();

    private BackgroundSocketService service;
    private AppStateChecker appStateChecker;
    private Context context;
    private String token;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "Connected to service");
            service = ((BackgroundSocketService.LocalBinder) binder).getService();
            service.onBind();
            serviceBoundSubject.onNext(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from service");
            service = null;
            serviceUnboundSubject.onNext(null);
        }
    };

    public SocketServiceBinder(Context context) {
        this.context = context;

        appStateChecker = new AppStateChecker();
        appStateChecker.onAppInBackground()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe($ -> {
                Log.d(TAG, "App is in background -> stopping socket service");
                stop(true);
            });
    }

    public boolean isBound() {
        return service != null;
    }

    public SocketService getService() {
        return service;
    }

    public Observable<BackgroundSocketService> onBindService() {
        return serviceBoundSubject;
    }

    public Observable<Void> onUnbindService() {
        return serviceUnboundSubject;
    }

    public void start(String token) {
        if (isBound()) {
            return;
        }

        this.token = token;
        Intent intent = prepareServiceStartIntent(token);
        context.bindService(intent, connection, context.BIND_AUTO_CREATE);
        context.startService(intent);
    }

    @NonNull
    private Intent prepareServiceStartIntent(String token) {
        Intent intent = new Intent("START_SERVICE");
        intent.putExtra("token", token);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    public void stop(boolean closeService) {
        if (isBound()) {
            context.unbindService(connection);
        }

        if (closeService) {
            Intent intent = new Intent("START_SERVICE");
            intent.setPackage(context.getPackageName());
            try {
                context.stopService(intent);
            } catch (IllegalArgumentException e) {
                // Service is not registered -> do nothing
            }
        }

        service = null;
        serviceUnboundSubject.onNext(null);
    }

    public void resume() {
        if (! isBound()) {
            if (token != null) {
                // Restart
                start(token);
            }
        } else {
            service.resume();
            appStateChecker.onResume();
        }
    }

    public void pause() {
        if (isBound()) {
            appStateChecker.onPause();
            service.pause();
        }
    }
}
