package com.example.nasko.whisper.chats.di.modules;

import android.content.Context;

import com.example.nasko.whisper.chats.ToolbarContract;
import com.example.nasko.whisper.chats.ToolbarPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatsToolbarPresenterModule {

    private ToolbarContract.View view;

    public ChatsToolbarPresenterModule(ToolbarContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    ToolbarContract.Presenter providePresenter(Context context, SocketService socketService, UserProvider userProvider) {
        return new ToolbarPresenter(view, context, socketService, userProvider);
    }
}
