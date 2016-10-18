package com.example.nasko.whisper.data.notifications;

import com.example.nasko.whisper.AppState;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.data.JsonDeserializer;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import javax.inject.Inject;

public class FcmListenerService extends FirebaseMessagingService {

    @Inject MessageNotificationController notificationController;
    @Inject JsonDeserializer deserializer;
    @Inject UserProvider userProvider;
    @Inject AppState appState;

    @Override
    public void onCreate() {
        super.onCreate();
        WhisperApplication.baseComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String msgJson = remoteMessage.getData().get("payload");
        try {
            Message message = deserializer.deserialize(msgJson, Message.class);
            User currentUser = userProvider.getCurrentUser();
            if (currentUser == null) {
                // TODO: No logged user -> unsubscribe and clear local storage
                return;
            }

            boolean isAuthor = currentUser.getUId().equals(message.getAuthor().getId());
            if (!isAuthor && appState.inBackground()) {
                notificationController.createMessageNotification(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
