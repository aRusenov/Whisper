package com.example.nasko.whisper.network.notifications.consumer;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.MessageNotificationController;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.network.JsonDeserializer;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class FcmListenerService extends FirebaseMessagingService {

    private MessageNotificationController notificationController;
    private JsonDeserializer deserializer;
    private UserProvider userProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationController = WhisperApplication.instance().getNotificationController();
        deserializer = new JsonDeserializer();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String msgJson = remoteMessage.getData().get("payload");
        try {
            Message message = deserializer.deserialize(msgJson, Message.class);
            boolean isAuthor = userProvider.getCurrentUser().getUId().equals(message.getAuthor().getId());
            boolean isInBackground = true; // TODO: Check if app is in background
            if (!isAuthor && isInBackground) {
                notificationController.createMessageNotification(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
