package com.example.nasko.whisper.network.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.managers.ConfigLoader;
import com.example.nasko.whisper.managers.NotificationController;

public class BackgroundSocketService extends Service implements MessageBroadcaster {

    private static final String TAG = "BackgroundSocketService";

    private NotificationController notificationController;
    private SocketService socketService;
    private String token;
    private Messenger client;
    private Messenger messenger;

    @Override
    public void sendMessage(Message message) {
        if (client != null) {
            try {
                client.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "Service client has disconnected");
                client = null;
            }
        } else {
            if (message.what == MessageTypes.MSG_NEW_MESSAGE) {
                notificationController.createMessageNotification((com.example.nasko.whisper.models.Message) message.obj);
                Log.e(TAG, "Client is null - showing notification");
            }
        }
    }

    public class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageTypes.MSG_REGISTER_CLIENT:
                    if (client != null) {
                        throw new UnsupportedOperationException("Only one client can connect");
                    }
                    client = msg.replyTo;
                    break;
                case MessageTypes.MSG_UNREGISTER_CLIENT:
                    if (client == null) {
                        throw new UnsupportedOperationException("Cannot unregister null client");
                    }
                    client = null;
                    break;
                case MessageTypes.MSG_SHOW_CHATS:
                    socketService.getContactsService().loadContacts();
                    break;
                case MessageTypes.MSG_STOP_SERVICE:
                    stopSelf();
                    break;
                case MessageTypes.MSG_ADD_CONTACT:
                    String contactId = (String) msg.obj;
                    socketService.getContactsService().addContact(contactId);
                    break;
                case MessageTypes.MSG_LOAD_MESSAGES:
                    Bundle data = msg.getData();
                    socketService.getMessagesService().loadMessages(data.getString("chatId"), data.getInt("from"), data.getInt("limit"));
                    break;
                case MessageTypes.MSG_SEND_MESSAGE:
                    com.example.nasko.whisper.models.Message message = (com.example.nasko.whisper.models.Message) msg.obj;
                    socketService.getMessagesService().sendMessage(message.getChatId(), message.getText());
                    break;
                default:
                    // TODO: Return invalid message
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messenger = new Messenger(new IncomingHandler());

        Intent notificationIntent = new Intent(this, BackgroundSocketService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Whisper")
                .setContentText("is whispering for you..")
                .setContentIntent(pendingIntent).build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationController = new NotificationController(this);
        if (intent != null && intent.hasExtra("token")) {
            token = intent.getStringExtra("token");
        }

        if (token == null) {
            Log.w(TAG, "Token is null, stopping service");
            stopSelf();
        }

        Log.d(TAG, "Service started");
        if (socketService == null) {
            Log.d(TAG, "Socket service is null - recreating it");
            String endpoint = ConfigLoader.getConfigValue(this, "api_url");
            socketService = new SocketService(endpoint, this);
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
}
