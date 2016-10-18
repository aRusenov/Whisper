package com.example.nasko.whisper.chatroom.di.modules;

import com.example.nasko.whisper.chatroom.ChatroomContract;
import com.example.nasko.whisper.chatroom.ChatroomPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.notifications.MessageNotificationController;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.models.view.ChatViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatroomPresenterModule {

    private ChatroomContract.View view;
    private ChatViewModel chat;
    private boolean restoredState;

    public ChatroomPresenterModule(ChatroomContract.View view, ChatViewModel chat, boolean restoredState) {
        this.view = view;
        this.chat = chat;
        this.restoredState = restoredState;
    }

    @Provides @ActivityScope
    ChatroomContract.Presenter providePresenter(SocketService socketService,
                                                MessageNotificationController notificationController,
                                                UserProvider userProvider) {
        return new ChatroomPresenter(view, chat, restoredState, socketService, notificationController, userProvider);
    }
}
