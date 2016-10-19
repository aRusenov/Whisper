package com.example.nasko.whisper.chats.di.modules;

import com.example.nasko.whisper.chats.ChatsContract;
import com.example.nasko.whisper.chats.ChatsPresenter;
import com.example.nasko.whisper.chats.ViewCoordinator;
import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ChatsInteractorImpl;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractorImpl;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatsPresenterModule {

    private ChatsContract.View view;
    private ViewCoordinator viewCoordinator;

    public ChatsPresenterModule(ChatsContract.View view, ViewCoordinator viewCoordinator) {
        this.view = view;
        this.viewCoordinator = viewCoordinator;
    }

    @Provides @ActivityScope
    ChatsInteractor provideChatsInteractor(SocketService socketService, UserProvider userProvider) {
        return new ChatsInteractorImpl(socketService, userProvider);
    }

    @Provides @ActivityScope
    ContactsStateInteractor provideContactsStateInteractor(SocketService socketService, UserProvider userProvider) {
        return new ContactsStateInteractorImpl(socketService, userProvider);
    }

    @Provides
    @ActivityScope
    ChatsContract.Presenter provideChatsPresenter(ChatsInteractor chatsInteractor, ContactsStateInteractor connectionInteractor) {
        return new ChatsPresenter(view, viewCoordinator, chatsInteractor, connectionInteractor);
    }
}
