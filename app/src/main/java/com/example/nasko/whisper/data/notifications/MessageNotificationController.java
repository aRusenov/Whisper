package com.example.nasko.whisper.data.notifications;

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
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

public class MessageNotificationController implements NotificationDismissedListener {

    private Context context;
    private NotificationManager notificationManager;
    private Uri alarmSound;

    private int notificationIdCount;
    private SimpleArrayMap<String, Integer> chatsNotificationIds;

    public MessageNotificationController(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        chatsNotificationIds = new SimpleArrayMap<>();

        NotificationDismissedReceiver notificationDismissedReceiver = new NotificationDismissedReceiver(context, this);
        notificationDismissedReceiver.start();
    }

    public void createMessageNotification(Message message) {
        String chatId = message.getChatId();
        Integer notificationId = chatsNotificationIds.get(chatId);
        if (notificationId == null) {
            notificationId = notificationIdCount++;
            chatsNotificationIds.put(chatId, notificationId);
        }

        ChatViewModel chat = new ChatViewModel(chatId, Mapper.toMessageViewModel(message));
        chat.setDisplayContact(Mapper.toContactViewModel(message.getAuthor()));

        Notification notification = buildNotification(chat, notificationId);
        notificationManager.notify(notificationId, notification);
    }

    private Notification buildNotification(ChatViewModel chat, Integer notificationId) {
        // Add intent extras
        Intent mainActivityLaunchIntent = MainActivity.prepareIntent(context, chat);

        // Set MainActivity as entry point
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(mainActivityLaunchIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

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
        intent.putExtra(NotificationDismissedReceiver.EXTRA_CHAT_ID, contactId);

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

    public void removeNotification(String chatId) {
        Integer notificationId = chatsNotificationIds.remove(chatId);
        if (notificationId != null) {
            notificationManager.cancel(notificationId);
        }
    }
}
