package com.example.nasko.whisper.chats;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.utils.Navigator;
import com.google.firebase.messaging.FirebaseMessaging;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends SocketPresenter implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private ToolbarContract.View view;
    private Context context;
    private Navigator navigator;

    public ToolbarPresenter(ToolbarContract.View view, Context context, SocketService socketService,
                            UserProvider userProvider, Navigator navigator) {
        super(socketService, userProvider);
        this.view = view;
        this.context = context;
        this.navigator = navigator;

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

        Subscription disconnectSub = socketService.connectionService()
                .onDisconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    Log.d(TAG, "Disconnect");
                    view.setNetworkStatus("Connecting..");
                });

        subscriptions.add(disconnectSub);
    }

    @Override
    public void onSettingsClicked() {
        User currentUser = userProvider.getCurrentUser();
        navigator.navigateToProfileScreen(context, currentUser);
    }

    @Override
    public void onLogoutClicked() {
        logout();
    }

    private void logout() {
        userProvider.logout();
        socketService.destroy();
        navigator.navigateToLoginScreen(context);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userProvider.getCurrentUser().getUId());
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }
}
