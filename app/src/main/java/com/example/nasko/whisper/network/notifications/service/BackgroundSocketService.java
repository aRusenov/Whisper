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

public class BackgroundSocketService extends Service implements OnNewMessageListener{

    @Override
    public void onNewMessage(Message message) {
        if (isPaused) {
            notificationController.createMessageNotification(message);
        }
    }

    public void onBind() {
        if (! socketService.connected() && token != null) {
            socketService.reconnect();
        }
    }

    public class LocalBinder extends Binder {
        public BackgroundSocketService getService() {
            return BackgroundSocketService.this;
        }
    }

    private static final String TAG = BackgroundSocketService.class.getName();

    private final IBinder binder = new LocalBinder();
    private NotificationController notificationController;
    private NetworkStateReceiver networkStateReceiver;

    private SocketService socketService;
    private String token;
    private boolean isPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        String endpoint = ConfigLoader.getConfigValue(this, "api_url");

        socketService = new SocketService(endpoint);
        socketService.getMessagesService().setNewMessageEventListener(this);
        notificationController = new NotificationController(this);
        networkStateReceiver = new NetworkStateReceiver(this) {
            @Override
            public void onNetworkConnected() {
                if (! socketService.connected() && token != null) {
                    socketService.reconnect();
                }
            }

            @Override
            public void onNoNetworkConnectivity() {
                // TODO: Report
            }
        };
        networkStateReceiver.start();

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

    public SocketService getSocketService() {
        return socketService;
    }

    public ContactsService getContactsService() {
        return socketService.getContactsService();
    }

    public MessagesService getMessagesService() {
        return socketService.getMessagesService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        detachListeners();

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationController = new NotificationController(this);
        token = intent.getStringExtra("token");
        socketService.setToken(token);

        Log.d(TAG, "Service started");
        if (! socketService.connected()) {
            socketService.connect();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketService.dispose();
        networkStateReceiver.stop();
        Log.d(TAG, "Service destroyed");
    }

    public void detachListeners() {
        socketService.getMessagesService().clearListeners();
        socketService.getContactsService().clearListeners();
    }
}
