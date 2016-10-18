package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;

import rx.Observable;

public class ConnectionInteractorImpl implements ConnectionInteractor {

    private SocketService socketService;

    public ConnectionInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        User currentUser = userProvider.getCurrentUser();
        if (currentUser != null && currentUser.getSessionToken() != null) {
            socketService.start(currentUser.getSessionToken());
        }
    }

    @Override
    public Observable onConnected() {
        return socketService.connectionService().onConnect();
    }

    @Override
    public Observable onConnecting() {
        return socketService.connectionService().onConnecting();
    }

    @Override
    public Observable<User> onAuthenticated() {
        return socketService.connectionService().onAuthenticated();
    }
}
