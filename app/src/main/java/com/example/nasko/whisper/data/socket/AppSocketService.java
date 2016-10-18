package com.example.nasko.whisper.data.socket;

import android.content.Context;
import android.util.Log;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class AppSocketService implements SocketService {

    private static final String TAG = "AppSocketService";

    private NetworkStateReceiver networkStateReceiver;
    private CompositeSubscription subscriptions;
    private String token;

    private SocketManager socketManager;
    private ConnectionService connectionService;
    private MessagesService messagesService;
    private ContactsService contactsService;

    private boolean authenticated;

    public AppSocketService(SocketManager socketManager, ConnectionService connectionService,
                            ContactsService contactsService, MessagesService messagesService, Context context) {
        this.socketManager = socketManager;
        this.connectionService = connectionService;
        this.contactsService = contactsService;
        this.messagesService = messagesService;
        setOwnSocketListeners();

        networkStateReceiver = new NetworkStateReceiver(context.getApplicationContext()) {
            @Override
            public void onNetworkConnected() {
                if (! socketManager.connected() && token != null) {
                    reconnect();
                }
            }

            @Override
            public void onNoNetworkConnectivity() {
                // TODO: Report
            }
        };

        networkStateReceiver.start();
    }

    private void setOwnSocketListeners() {
        subscriptions = new CompositeSubscription();
        Subscription connectSub = connectionService().onConnect()
                .subscribe($ -> {
                    Log.d(TAG, "Connected");
                    if (token != null) {
                        connectionService.authenticate(token);
                    }
                });

        Subscription authSub = connectionService().onAuthenticated()
                .subscribe(user -> {
                    Log.d(TAG, "Authenticated");
                    authenticated = true;
                });

        Subscription disconnectSub = connectionService().onDisconnect()
                .subscribe($ -> {
                    Log.d(TAG, "Disconnected");
                    authenticated = false;
                    if (networkStateReceiver.isConnected()) {

                    }
                });

        subscriptions.add(connectSub);
        subscriptions.add(authSub);
        subscriptions.add(disconnectSub);
    }

    @Override
    public void start(String userToken) {
        token = userToken;
        if (! socketManager.connected()) {
            reconnect();
        }
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Service destroyed");
        subscriptions.clear();
        socketManager.dispose();
        networkStateReceiver.stop();
    }

    @Override
    public ConnectionService connectionService() {
        return connectionService;
    }

    @Override
    public ContactsService contactsService() {
        return contactsService;
    }

    @Override
    public MessagesService messageService() {
        return messagesService;
    }

    public boolean authenticated() {
        return authenticated;
    }

    private void reconnect() {
        socketManager.connect();
    }
}
