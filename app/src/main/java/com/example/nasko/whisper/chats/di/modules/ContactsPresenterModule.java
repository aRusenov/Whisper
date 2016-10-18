package com.example.nasko.whisper.chats.di.modules;

import com.example.nasko.whisper.chats.ContactsContract;
import com.example.nasko.whisper.chats.ContactsPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ContactsPresenterModule {

    private ContactsContract.View view;

    public ContactsPresenterModule(ContactsContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    ContactsContract.Presenter provideContactsPresenter(SocketService socketService, UserProvider userProvider) {
        return new ContactsPresenter(view, socketService, userProvider);
    }
}
