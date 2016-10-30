package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.data.notifications.MessageNotificationController;
import com.example.nasko.whisper.models.view.ChatViewModel;

public class NotificationDismissInteractorImpl implements NotificationDismissInteractor {

    private MessageNotificationController notificationController;
    private ChatViewModel chat;

    public NotificationDismissInteractorImpl(MessageNotificationController notificationController, ChatViewModel chat) {
        this.notificationController = notificationController;
        this.chat = chat;
    }

    @Override
    public void init() {
        notificationController.removeNotification(chat.getId());
    }

    @Override
    public void destroy() { }
}
