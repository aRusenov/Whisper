package com.example.nasko.whisper.chats;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.google.firebase.messaging.FirebaseMessaging;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends SocketPresenter implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private ToolbarContract.View view;
    private Context context;

    public ToolbarPresenter(ToolbarContract.View view, Context context,
                            SocketService socketService, UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
        this.context = context;

        User currentUser = userProvider.getCurrentUser();
        // Subscribe to firebase cloud messaging service
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUId());

        initListeners();
    }

    private void initListeners() {
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
    public void onSettingsClicked() {
        User currentUser = userProvider.getCurrentUser();
        view.navigateToSettings(currentUser);
    }

    @Override
    public void onLogoutClicked() {
        userProvider.logout();
        socketService.destroy();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userProvider.getCurrentUser().getUId());

        view.navigateToLoginScreen();
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }
}
