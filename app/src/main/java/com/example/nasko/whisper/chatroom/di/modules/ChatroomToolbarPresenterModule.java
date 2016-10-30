package com.example.nasko.whisper.chatroom.di.modules;

import com.example.nasko.whisper.chatroom.ToolbarContract;
import com.example.nasko.whisper.chatroom.ToolbarPresenter;
import com.example.nasko.whisper.chatroom.interactors.ContactStateInteractor;
import com.example.nasko.whisper.chatroom.interactors.ContactStateInteractorImpl;
import com.example.nasko.whisper.dagger.ActivityScope;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.view.ChatViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ChatroomToolbarPresenterModule {

    private ToolbarContract.View view;
    private ChatViewModel chat;

    public ChatroomToolbarPresenterModule(ToolbarContract.View view, ChatViewModel chat) {
        this.view = view;
        this.chat = chat;
    }

    @Provides @ActivityScope ContactStateInteractor provideContactStateInteractor(SocketService socketService) {
        return new ContactStateInteractorImpl(socketService, chat);
    }

    @Provides @ActivityScope ToolbarContract.Presenter providePresenter(ContactStateInteractor contactStateInteractor) {
        return new ToolbarPresenter(view, contactStateInteractor);
    }

}
