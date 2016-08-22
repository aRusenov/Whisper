package com.example.nasko.whisper.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class NotificationDismissedReceiver  {

    public static final String ACTION_DISMISSED = NotificationDismissedReceiver.class.getPackage().getName() + "ACTION_DISMISSED";
    public static final String EXTRA_CONTACT_ID = "contactId";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String contactId = intent.getStringExtra(EXTRA_CONTACT_ID);
            if (contactId == null) {
                return;
            }

            listener.onNotificationDismissed(contactId);
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
