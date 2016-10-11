package com.example.nasko.whisper;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;

import rx.subscriptions.CompositeSubscription;

public abstract class SocketPresenter implements BasePresenter {

    protected SocketService socketService;
    protected UserProvider userProvider;
    protected CompositeSubscription subscriptions;

    protected SocketPresenter(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        this.userProvider = userProvider;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        subscriptions.clear();
    }
}
