package com.example.nasko.whisper.data.socket.consumer;

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
import com.example.nasko.whisper.chats.MainActivity;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

public class MessageNotificationController implements NotificationDismissedListener {

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

        NotificationDismissedReceiver notificationDismissedReceiver = new NotificationDismissedReceiver(context, this);
        notificationDismissedReceiver.start();
    }

    public void createMessageNotification(Message message) {
        String contactId = message.getAuthor().getId();
        Integer notificationId = contactsNotificationIds.get(contactId);
        if (notificationId == null) {
            notificationId = notificationIdCount++;
            contactsNotificationIds.put(contactId, notificationId);
        }

        ChatViewModel chat = new ChatViewModel(message.getChatId(), Mapper.toMessageViewModel(message));
        chat.setDisplayContact(Mapper.toContactViewModel(message.getAuthor()));

        Notification notification = buildNotification(chat, notificationId);

        notificationManager.notify(notificationId, notification);
    }

    private Notification buildNotification(ChatViewModel chat, int notificationId) {
        // Add intent extras
        Intent chatroomLaunchIntent = new Intent(context, MainActivity.class);
        chatroomLaunchIntent.putExtra("chat", chat);
        chatroomLaunchIntent.putExtra("user", userProvider.getCurrentUser());

        // Add backstack with parent activity (for navigating back)
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(chatroomLaunchIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.profile)
                .setContentTitle(chat.getDisplayContact().getUsername())
                .setContentText(chat.getLastMessage().getText())
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setDeleteIntent(createOnDismissedIntent(chat.getDisplayContact().getId(), notificationId));

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
