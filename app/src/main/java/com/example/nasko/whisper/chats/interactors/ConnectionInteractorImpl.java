package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;

import rx.Observable;

public class ConnectionInteractorImpl implements ConnectionInteractor {

    private SocketService socketService;

    public ConnectionInteractorImpl(SocketService socketService) {
        this.socketService = socketService;
    }

    @Override
    public Observable<String> onConnectionProblem() {
        return Observable.merge(socketService.connectionService().onConnecting(),
                socketService.connectionService().onConnect(),
                socketService.connectionService().onDisconnect());
    }

    @Override
    public Observable<String> onConnected() {
        return socketService.connectionService()
                .onConnect();
    }

    @Override
    public Observable<User> onAuthenticated() {
        return socketService.connectionService()
                .onAuthenticated();
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
