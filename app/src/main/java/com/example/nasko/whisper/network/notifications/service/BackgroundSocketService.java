package com.example.nasko.whisper.network.notifications.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.managers.NotificationController;
import com.example.nasko.whisper.models.Message;

import java.net.URISyntaxException;

public class BackgroundSocketService extends Service{

    public class LocalBinder extends Binder {
        public BackgroundSocketService getService() {
            return BackgroundSocketService.this;
        }
    }

    private static final String TAG = BackgroundSocketService.class.getName();

    private final IBinder binder = new LocalBinder();
    private NotificationController notificationController;
    private NetworkStateReceiver networkStateReceiver;
    private ConnectionService connectionService;
    private MessagesService messagesService;
    private ContactsService contactsService;

    private SocketManager socketManager;
    private String token;
    private boolean isPaused;
    private boolean isClosing;

    @Override
    public void onCreate() {
        super.onCreate();
        String endpoint = ConfigLoader.getConfigValue(this, "api_url");
        try {
            socketManager = new SocketManager(endpoint);
        } catch (URISyntaxException e) {
            Log.wtf(TAG, "Invalid socket endpoint :(");
            stopSelf();
        }

        connectionService = new HerokuConnectionService(socketManager);
        contactsService = new HerokuContactsService(socketManager);
        messagesService = new HerokuMessagesService(socketManager);
        setOwnSocketListeners();

        notificationController = new NotificationController(this);
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
        startServiceInForeground();
    }

    private void setOwnSocketListeners() {
        socketManager.on(HerokuConnectionService.EVENT_CONNECT, Object.class, args -> {
            if (token != null) {
                connectionService.authenticate(token);
            }
        });

        socketManager.on(HerokuMessagesService.EVENT_NEW_MESSAGE, Message.class, message -> {
            if (isPaused) {
                notificationController.createMessageNotification(message);
            }
        });
    }

    public void onBind() {
        if (! socketManager.connected() && token != null) {
            reconnect();
        }
    }

    private void startServiceInForeground() {
        Intent notificationIntent = new Intent(this, BackgroundSocketService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Whisper")
                .setContentText("is listening.")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public ContactsService getContactsService() {
        return contactsService;
    }

    public MessagesService getMessagesService() {
        return messagesService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void pause() {
        isPaused = true;
        if (networkStateReceiver.isConnected() && !socketManager.connected()) {
            reconnect();
        }
    }

    public void resume() {
        isPaused = false;
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
    public boolean onUnbind(Intent intent) {
        detachListeners();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        isClosing = false;
        token = intent.getStringExtra("token");

        if (! socketManager.connected()) {
            reconnect();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachListeners();
        socketManager.disconnect();
        networkStateReceiver.stop();
        isClosing = true;

        Log.d(TAG, "Service destroyed");
    }

    public void detachListeners() {
        connectionService.clearListeners();
        contactsService.clearListeners();
        messagesService.clearListeners();
    }
}
