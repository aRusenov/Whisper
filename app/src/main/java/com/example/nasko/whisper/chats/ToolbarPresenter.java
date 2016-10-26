package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.google.firebase.messaging.FirebaseMessaging;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends SocketPresenter implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private ToolbarContract.View view;

    public ToolbarPresenter(ToolbarContract.View view,
                            SocketService socketService, UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
    }

    @Override
    public void init() {
        // Subscribe to FCM <user-id> topic
        FirebaseMessaging.getInstance().subscribeToTopic(userProvider.getCurrentUser().getUId());

        Subscription connectSub = socketService.connectionService()
                .onConnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Connected");
                    view.setNetworkStatus("Authenticating...");
                });

        subscriptions.add(connectSub);

        Subscription authSub = socketService.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Authenticated");
                    view.setNetworkStatus("Whisper");
                });

        subscriptions.add(authSub);

        Subscription connectingSub = socketService.connectionService().onConnecting()
                .mergeWith(socketService.connectionService().onConnect())
                .mergeWith(socketService.connectionService().onDisconnect())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    view.setNetworkStatus("Connecting..");
                });

        subscriptions.add(connectingSub);
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }

    @Override
    public void onContactsClicked() {
        view.navigateToContacts();
    }

    @Override
    public void onSettingsClicked() {
        view.navigateToSettings(userProvider.getCurrentUser());
    }

    @Override
    public void onLogoutClicked() {
        userProvider.logout();
        socketService.destroy();
        // Unsubscribe from FCM topic
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userProvider.getCurrentUser().getUId());

        view.navigateToLoginScreen();
    }
}
