package com.example.nasko.whisper.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.util.SimpleArrayMap;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.activities.ChatroomActivity;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;

public class MessageNotificationController implements NotificationDismissedListener {

    private NotificationDismissedReceiver notificationDismissedReceiver;
    private UserProvider userProvider;
    private Context context;
    private NotificationManager notificationManager;
    private Uri alarmSound;

    private int notificationIdCount;
    private SimpleArrayMap<String, Integer> contactsNotificationIds;

    public MessageNotificationController(UserProvider userProvider, Context context) {
        this.context = context;
        this.userProvider = userProvider;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        contactsNotificationIds = new SimpleArrayMap<>();

        notificationDismissedReceiver = new NotificationDismissedReceiver(context, this);
        notificationDismissedReceiver.start();
    }

    public void createMessageNotification(Message message) {
        String contactId = message.getAuthor().getId();
        Integer notificationId = contactsNotificationIds.get(contactId);
        if (notificationId == null) {
            notificationId = notificationIdCount++;
            contactsNotificationIds.put(contactId, notificationId);
        }

        Chat chat = new Chat();
        chat.setId(message.getChatId());
        chat.setOtherContact(message.getAuthor());

        Notification notification = buildNotification(message, chat, notificationId);

        notificationManager.notify(notificationId, notification);
    }

    private Notification buildNotification(Message message, Chat chat, int notificationId) {
        // Add intent extras
        Intent chatroomLaunchIntent = new Intent(context, ChatroomActivity.class);
        chatroomLaunchIntent.putExtra("chat", chat);
        chatroomLaunchIntent.putExtra("user", userProvider.getCurrentUser());

        // Add backstack with parent activity (for navigating back)
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ChatroomActivity.class);
        stackBuilder.addNextIntent(chatroomLaunchIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.profile)
                .setContentTitle(message.getAuthor().getUsername())
                .setContentText(message.getText())
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setDeleteIntent(createOnDismissedIntent(chat.getOtherContact().getId(), notificationId));

        return builder.build();
    }

    private PendingIntent createOnDismissedIntent(String contactId, int notificationId) {
        Intent intent = new Intent(NotificationDismissedReceiver.ACTION_DISMISSED);
        intent.putExtra(NotificationDismissedReceiver.EXTRA_CONTACT_ID, contactId);

        return PendingIntent.getBroadcast(
                context.getApplicationContext(),
                notificationId,
                intent,
                0);
    }

    @Override
    public void onNotificationDismissed(String contactId) {
        removeNotification(contactId);
    }

    public void removeNotification(String contactId) {
        Integer notificationId = contactsNotificationIds.remove(contactId);
        if (notificationId != null) {
            notificationManager.cancel(notificationId);
        }
    }
}
