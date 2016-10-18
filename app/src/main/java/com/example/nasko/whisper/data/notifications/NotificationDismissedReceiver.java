package com.example.nasko.whisper.data.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class NotificationDismissedReceiver  {

    public static final String ACTION_DISMISSED = NotificationDismissedReceiver.class.getPackage().getName() + "ACTION_DISMISSED";
    public static final String EXTRA_CHAT_ID = "chatId";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chatId = intent.getStringExtra(EXTRA_CHAT_ID);
            if (chatId != null) {
                listener.onNotificationDismissed(chatId);
            }
        }
    };

    private NotificationDismissedListener listener;
    private Context context;

    public NotificationDismissedReceiver(Context context, NotificationDismissedListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DISMISSED);
        context.registerReceiver(receiver, intentFilter);
    }

    public void stop() {
        context.unregisterReceiver(receiver);
    }
}
