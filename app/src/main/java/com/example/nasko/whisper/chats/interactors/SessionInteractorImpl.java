package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.google.firebase.messaging.FirebaseMessaging;

public class SessionInteractorImpl implements SessionInteractor {

    private UserProvider userProvider;
    private SocketService socketService;

    public SessionInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.userProvider = userProvider;
        this.socketService = socketService;
    }

    @Override
    public void init() {
        FirebaseMessaging.getInstance().subscribeToTopic(userProvider.getCurrentUser().getUId());
    }

    @Override
    public void destroy() { }

    @Override
    public void logoutUser() {
        userProvider.logout();
        socketService.destroy();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userProvider.getCurrentUser().getUId());
    }
}
