package com.example.nasko.whisper.managers;

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

public class MessageNotificationController {

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
    }

    public void createMessageNotification(Message message) {
        String contactId = message.getAuthor().getId();
        Integer notificationId = contactsNotificationIds.get(contactId);
        if (notificationId == null) {
            notificationId = notificationIdCount++;
            contactsNotificationIds.put(contactId, notificationId);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.profile)
                        .setContentTitle(message.getAuthor().getUsername())
                        .setContentText(message.getText());

        mBuilder.setSound(alarmSound);

        Chat chat = new Chat();
        chat.setId(message.getChatId());
        chat.setOtherContact(message.getAuthor());

        // Creates an explicit intent for an Activity in your app
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

        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
