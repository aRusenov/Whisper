package com.example.nasko.whisper.dagger.user;

import android.content.Context;

import com.example.nasko.whisper.data.JsonDeserializer;
import com.example.nasko.whisper.data.socket.AppConnectionService;
import com.example.nasko.whisper.data.socket.AppContactsService;
import com.example.nasko.whisper.data.socket.AppMessagesService;
import com.example.nasko.whisper.data.socket.AppSocketService;
import com.example.nasko.whisper.data.socket.ConnectionService;
import com.example.nasko.whisper.data.socket.ContactsService;
import com.example.nasko.whisper.data.socket.MessagesService;
import com.example.nasko.whisper.data.socket.SocketManager;
import com.example.nasko.whisper.data.socket.SocketService;

import java.net.URISyntaxException;

import dagger.Module;
import dagger.Provides;

@Module
public class SocketModule {

    private String socketEndpoint;

    public SocketModule(String socketEndpoint) {
        this.socketEndpoint = socketEndpoint;
    }

    @Provides @UserScope SocketManager provideSocketManager(JsonDeserializer deserializer) {
        try {
            return new SocketManager(socketEndpoint, deserializer);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Provides @UserScope ConnectionService provideConnectionService(SocketManager socketManager) {
        return new AppConnectionService(socketManager);
    }

    @Provides @UserScope ContactsService provideContactsService(SocketManager socketManager) {
        return new AppContactsService(socketManager);
    }

    @Provides @UserScope MessagesService provideMessagesService(SocketManager socketManager) {
        return new AppMessagesService(socketManager);
    }

    @Provides @UserScope SocketService provideSocketService(SocketManager socketManager,
                                                            ConnectionService connectionService,
                                                            ContactsService contactsService,
                                                            MessagesService messagesService,
                                                            Context context) {
        return new AppSocketService(socketManager, connectionService, contactsService, messagesService, context);
    }
}
