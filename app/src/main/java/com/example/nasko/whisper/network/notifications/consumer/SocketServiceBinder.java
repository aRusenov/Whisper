package com.example.nasko.whisper.network.notifications.consumer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.nasko.whisper.network.notifications.service.BackgroundSocketService;
import com.example.nasko.whisper.network.notifications.service.SocketService;

import java.util.ArrayList;
import java.util.List;

public class SocketServiceBinder {

    private static final String TAG = SocketServiceBinder.class.getName();

    private Context context;
    private boolean isBound;
    private BackgroundSocketService service;
    private List<OnServiceBoundListener> serviceBoundListeners;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "Connected to service");
            service = ((BackgroundSocketService.LocalBinder) binder).getService();
            isBound = true;
            service.onBind();
            signalBinding(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from service");
            service = null;
            isBound = false;
            signalUnbinding();
        }
    };

    public SocketServiceBinder(Context context) {
        this.context = context;
        this.serviceBoundListeners = new ArrayList<>();
    }

    public boolean isBound() {
        return isBound;
    }

    public SocketService getService() {
        return service;
    }

    public void addServiceBoundListener(OnServiceBoundListener listener) {
        serviceBoundListeners.add(listener);
    }

    public boolean removeServiceBoundListener(OnServiceBoundListener listener) {
        return serviceBoundListeners.remove(listener);
    }

    public void start(String token) {
        if (!isBound) {
            Intent intent = new Intent("START_SERVICE");
            intent.putExtra("token", token);
            intent.setPackage(context.getPackageName());
            context.bindService(intent, connection, context.BIND_AUTO_CREATE);
            context.startService(intent);
        }
    }

    public void stop(boolean closeService) {
        if (isBound) {
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
        isBound = false;
        signalUnbinding();
    }

    public void resume() {
        if (service != null) {
            service.resume();
        }
    }

    public void pause() {
        if (service != null) {
            service.pause();
        }
    }

    private void signalBinding(SocketService boundService) {
        for (OnServiceBoundListener listener : serviceBoundListeners) {
            listener.onServiceBind(boundService);
        }
    }

    private void signalUnbinding() {
        for (OnServiceBoundListener listener : serviceBoundListeners) {
            listener.onServiceUnbind();
        }
    }
}
