package com.example.nasko.whisper.dagger.base;

import com.example.nasko.whisper.SplashActivity;
import com.example.nasko.whisper.chatroom.ChatroomActivity;
import com.example.nasko.whisper.chats.MainActivity;
import com.example.nasko.whisper.contacts.ContactsActivity;
import com.example.nasko.whisper.data.notifications.FcmListenerService;
import com.example.nasko.whisper.dagger.rest.RestComponent;
import com.example.nasko.whisper.dagger.rest.RestModule;
import com.example.nasko.whisper.dagger.user.SocketModule;
import com.example.nasko.whisper.dagger.user.UserComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, AppStateModule.class, DeserializationModule.class, NotificationModule.class})
public interface BaseComponent {

    UserComponent plus(SocketModule userModule);

    RestComponent plus(RestModule restModule);

    void inject(SplashActivity splashActivity);

    void inject(MainActivity activity);

    void inject(ChatroomActivity activity);

    void inject(ContactsActivity activity);

    void inject(FcmListenerService service);
}
