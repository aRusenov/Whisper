package com.example.nasko.whisper.chats.di.modules;

import com.example.nasko.whisper.chats.ChatsContract;
import com.example.nasko.whisper.chats.ChatsPresenter;
import com.example.nasko.whisper.chats.ViewCoordinator;
import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsInteractor;
import com.example.nasko.whisper.chats.interactors.MessagesInteractor;
import com.example.nasko.whisper.dagger.ActivityScope;

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

    @Provides
    @ActivityScope
    ChatsContract.Presenter provideChatsPresenter(ChatsInteractor chatsInteractor, ConnectionInteractor connectionInteractor,
                                                  MessagesInteractor messagesInteractor, ContactsInteractor contactsInteractor) {
        return new ChatsPresenter(view, viewCoordinator, chatsInteractor, connectionInteractor, messagesInteractor, contactsInteractor);
    }
}
