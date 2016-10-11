package com.example.nasko.whisper.chats;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.utils.Navigator;
import com.example.nasko.whisper.ServiceBoundPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;
import com.google.firebase.messaging.FirebaseMessaging;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ToolbarPresenter extends ServiceBoundPresenter implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private ToolbarContract.View view;
    private Context context;
    private Navigator navigator;

    public ToolbarPresenter(ToolbarContract.View view, Context context,
                            UserProvider userProvider, Navigator navigator,
                            SocketServiceBinder socketServiceBinder) {
        super(socketServiceBinder, userProvider);
        this.view = view;
        this.context = context;
        this.navigator = navigator;

        User currentUser = userProvider.getCurrentUser();
        // Subscribe to firebase cloud messaging service
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUId());
        // Start background socket service
        serviceBinder.start(currentUser.getSessionToken());
    }

    @Override
    public void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions) {
        Subscription connectSub = service.connectionService()
                .onConnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Connected");
                    view.setNetworkStatus("Authenticating...");
                });

        serviceSubscriptions.add(connectSub);

        Subscription authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Authenticated");
                    view.setNetworkStatus("Whisper");
                });

        serviceSubscriptions.add(authSub);

        Subscription disconnectSub = service.connectionService()
                .onDisconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    Log.d(TAG, "Disconnect");
                    view.setNetworkStatus("Connecting..");
                });

        serviceSubscriptions.add(disconnectSub);
    }

    @Override
    public void onServiceUnbind() {}

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
        serviceBinder.stop(true);
        navigator.navigateToLoginScreen(context);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userProvider.getCurrentUser().getUId());
    }
}
