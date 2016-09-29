package com.example.nasko.whisper.network.notifications.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nasko.whisper.managers.ConfigLoader;

import java.net.URISyntaxException;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BackgroundSocketService extends Service implements SocketService {

    public class LocalBinder extends Binder {
        public BackgroundSocketService getService() {
            return BackgroundSocketService.this;
        }
    }

    private static final String TAG = "BackgroundSocketService";

    private final IBinder binder = new LocalBinder();
    private NetworkStateReceiver networkStateReceiver;

    private ConnectionService connectionService;
    private MessagesService messagesService;
    private ContactsService contactsService;

    private CompositeSubscription subscriptions;

    private SocketManager socketManager;
    private String token;
    private boolean isClosing;

    @Override
    public void onCreate() {
        Log.d(TAG, "Creating service");
        super.onCreate();
        String endpoint = ConfigLoader.getConfigValue(this, "api_url");
        try {
            socketManager = new SocketManager(endpoint);
        } catch (URISyntaxException e) {
            Log.wtf(TAG, "Invalid socket endpoint :(");
            stopSelf();
        }

        connectionService = new AppConnectionService(socketManager);
        contactsService = new AppContactsService(socketManager);
        messagesService = new AppMessagesService(socketManager);
        setOwnSocketListeners();

        networkStateReceiver = new NetworkStateReceiver(this) {
            @Override
            public void onNetworkConnected() {
                if (! socketManager.connected() && token != null) {
                    reconnect();
                }
            }

            @Override
            public void onNoNetworkConnectivity() {
                // TODO: Report
            }
        };

        networkStateReceiver.start();
    }

    private void setOwnSocketListeners() {
        subscriptions = new CompositeSubscription();
        Subscription connectSub = socketManager.on(AppConnectionService.EVENT_CONNECT, Object.class)
                .subscribe(o -> {
                    Log.d(TAG, "Connected");
                    if (token != null) {
                        connectionService.authenticate(token);
                    }
                });

        Subscription disconnectSub = socketManager.on(AppConnectionService.EVENT_DISCONNECT, String.class)
                .subscribe(o -> {
                    if (networkStateReceiver.isConnected()) {
                        socketManager.connect();
                    }
                });

        subscriptions.add(connectSub);
        subscriptions.add(disconnectSub);
    }

    public void onBind() {
        if (! socketManager.connected() && token != null) {
            reconnect();
        }
    }

    @Override
    public ConnectionService connectionService() {
        return connectionService;
    }

    @Override
    public ContactsService contactsService() {
        return contactsService;
    }

    @Override
    public MessagesService messageService() {
        return messagesService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void pause() {}

    public void resume() {
        if (networkStateReceiver.isConnected() && !socketManager.connected()) {
            reconnect();
        }
    }

    public void reconnect() {
        if (! isClosing) {
            socketManager.connect();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting service");
        isClosing = false;
        token = intent.getStringExtra("token");

        if (! socketManager.connected()) {
            reconnect();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        subscriptions.clear();
        socketManager.dispose();
        networkStateReceiver.stop();
        isClosing = true;

        Log.d(TAG, "Service destroyed");
    }
}
