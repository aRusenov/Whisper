package com.example.nasko.whisper.data.notifications;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.AppState;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.data.JsonDeserializer;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class FcmListenerService extends FirebaseMessagingService {

    private MessageNotificationController notificationController;
    private JsonDeserializer deserializer;
    private UserProvider userProvider;
    private AppState appState;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationController = WhisperApplication.instance().getNotificationController();
        userProvider = WhisperApplication.instance().getUserProvider();
        appState = WhisperApplication.instance().getAppState();
        deserializer = new JsonDeserializer();
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
