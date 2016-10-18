package com.example.nasko.whisper.chatroom.di.modules;

import com.example.nasko.whisper.chatroom.ToolbarContract;
import com.example.nasko.whisper.chatroom.ToolbarPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatroomToolbarPresenterModule {

    private ToolbarContract.View view;

    public ChatroomToolbarPresenterModule(ToolbarContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    ToolbarContract.Presenter providePresenter(SocketService socketService, UserProvider userProvider) {
        return new ToolbarPresenter(view, socketService, userProvider);
    }

}
