package com.example.nasko.whisper.network.notifications;

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

public class BackgroundSocketService extends Service {

    public class LocalBinder extends Binder {
        public BackgroundSocketService getService() {
            return BackgroundSocketService.this;
        }
    }

    private static final String TAG = BackgroundSocketService.class.getName();

    private NotificationController notificationController;
    private SocketService socketService;
    private String token;

    private final IBinder binder = new LocalBinder();

    public SocketService getSocketService() {
        return socketService;
    }

    public ContactsService getContactsService() {
        return socketService.getContactsService();
    }

    public MessagesService getMessagesService() {
        return socketService.getMessagesService();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String endpoint = ConfigLoader.getConfigValue(this, "api_url");
        socketService = new SocketService(endpoint);
        notificationController = new NotificationController(this);

        Intent notificationIntent = new Intent(this, BackgroundSocketService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Whisper")
                .setContentText("is whispering for you..")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationController = new NotificationController(this);
        token = intent.getStringExtra("token");

        Log.d(TAG, "Service started");
        if (socketService.getCurrentUser() == null) {
            socketService.connect();
            socketService.authenticate(token);
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketService.dispose();
        Log.d(TAG, "Service destroyed");
    }

    public void detachListeners() {
        socketService.getMessagesService().clearListeners();
        socketService.getContactsService().clearListeners();
    }
}
