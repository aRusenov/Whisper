package com.example.nasko.whisper.chats.di.modules;

import com.example.nasko.whisper.chats.ToolbarContract;
import com.example.nasko.whisper.chats.ToolbarPresenter;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractorImpl;
import com.example.nasko.whisper.chats.interactors.SessionInteractor;
import com.example.nasko.whisper.chats.interactors.SessionInteractorImpl;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatsToolbarPresenterModule {

    private ToolbarContract.View view;

    public ChatsToolbarPresenterModule(ToolbarContract.View view) {
        this.view = view;
    }

    @Provides @ActivityScope ConnectionInteractor provideConnectionInteractor(SocketService socketService) {
        return new ConnectionInteractorImpl(socketService);
    }

    @Provides @ActivityScope SessionInteractor provideSessionInteractor(SocketService socketService, UserProvider userProvider) {
        return new SessionInteractorImpl(socketService, userProvider);
    }

    @Provides @ActivityScope ToolbarContract.Presenter providePresenter(ConnectionInteractor connectionInteractor, SessionInteractor sessionInteractor) {
        return new ToolbarPresenter(view, connectionInteractor, sessionInteractor);
    }
}
