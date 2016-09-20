package com.example.nasko.whisper.network.notifications.consumer;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.AppState;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.MessageNotificationController;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.JsonDeserializer;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class FcmListenerService extends FirebaseMessagingService {

    private MessageNotificationController notificationController;
    private JsonDeserializer deserializer;
    private LocalUserRepository localUserRepository;
    private UserProvider userProvider;
    private AppState appState;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationController = WhisperApplication.instance().getNotificationController();
        localUserRepository = WhisperApplication.instance().getLocalUserRepository();
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
            User currentUser = getCurrentUser();
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

    private User getCurrentUser() {
        User user = userProvider.getCurrentUser();
        if (user == null) {
            user = localUserRepository.getLoggedUser();
        }

        userProvider.setCurrentUser(user);
        return user;
    }
}
