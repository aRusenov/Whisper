package com.example.nasko.whisper.chatroom.di.modules;

import com.example.nasko.whisper.chatroom.ChatroomContract;
import com.example.nasko.whisper.chatroom.ChatroomPresenter;
import com.example.nasko.whisper.chatroom.interactors.MessageSendInteractor;
import com.example.nasko.whisper.chatroom.interactors.MessageSendInteractorImpl;
import com.example.nasko.whisper.chatroom.interactors.MessagesLoadInteractor;
import com.example.nasko.whisper.chatroom.interactors.MessagesLoadInteractorImpl;
import com.example.nasko.whisper.chatroom.interactors.NotificationDismissInteractor;
import com.example.nasko.whisper.chatroom.interactors.NotificationDismissInteractorImpl;
import com.example.nasko.whisper.chatroom.interactors.TypingInteractor;
import com.example.nasko.whisper.chatroom.interactors.TypingInteractorImpl;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractorImpl;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.notifications.MessageNotificationController;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatroomPresenterModule {

    private ChatroomContract.View view;
    private ChatViewModel chat;
    private int lastLoadedMessageSeq;

    public ChatroomPresenterModule(ChatroomContract.View view, ChatViewModel chat, int lastLoadedMessageSeq) {
        this.view = view;
        this.chat = chat;
        this.lastLoadedMessageSeq = lastLoadedMessageSeq;
    }

    @Provides @ActivityScope @Named("userModel") ContactViewModel provideUserModel(UserProvider userProvider) {
        User user = userProvider.getCurrentUser();
        return new ContactViewModel(user.getUId(), user.getUsername(), user.getName(), user.getImage(), false);
    }

    @Provides @ActivityScope
    NotificationDismissInteractor provideNotificationDismissInteractor(MessageNotificationController notificationController) {
        return new NotificationDismissInteractorImpl(notificationController, chat);
    }

    @Provides @ActivityScope
    MessageSendInteractor provideMessageSendInteractor(SocketService socketService, @Named("userModel") ContactViewModel userModel) {
        return new MessageSendInteractorImpl(socketService, chat, userModel);
    }

    @Provides @ActivityScope
    ConnectionInteractor provideConnectionInteractor(SocketService socketService) {
        return new ConnectionInteractorImpl(socketService);
    }

    @Provides @ActivityScope
    MessagesLoadInteractor provideMessagesLoadInteractor(SocketService socketService) {
        return new MessagesLoadInteractorImpl(socketService, chat);
    }

    @Provides @ActivityScope TypingInteractor provideTypingInteractor(SocketService socketService, @Named("userModel") ContactViewModel userModel) {
        return new TypingInteractorImpl(socketService, userModel, chat);
    }

    @Provides @ActivityScope
    ChatroomContract.Presenter providePresenter(NotificationDismissInteractor notificationDismissInteractor,
                                                ConnectionInteractor connectionInteractor, MessagesLoadInteractor messagesLoadInteractor,
                                                MessageSendInteractor messageSendInteractor, TypingInteractor typingInteractor) {
        return new ChatroomPresenter(view, notificationDismissInteractor, connectionInteractor,
                messagesLoadInteractor, messageSendInteractor, typingInteractor, lastLoadedMessageSeq);
    }
}
