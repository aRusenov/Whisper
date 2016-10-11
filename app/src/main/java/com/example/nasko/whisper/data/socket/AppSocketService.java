package com.example.nasko.whisper.data.socket;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.utils.helpers.ConfigLoader;

import java.net.URISyntaxException;

import io.socket.client.Socket;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class AppSocketService implements SocketService {

    private static final String TAG = "AppSocketService";

    private NetworkStateReceiver networkStateReceiver;
    private CompositeSubscription subscriptions;
    private SocketManager socketManager;
    private String token;

    private ConnectionService connectionService;
    private MessagesService messagesService;
    private ContactsService contactsService;

    public AppSocketService(Context context) throws URISyntaxException {
        String endpoint = ConfigLoader.getConfigValue(context, "api_url");
        try {
            socketManager = new SocketManager(endpoint);
        } catch (URISyntaxException e) {
            Log.wtf(TAG, "Invalid socket endpoint :(");
            throw e;
        }

        connectionService = new AppConnectionService(socketManager);
        contactsService = new AppContactsService(socketManager);
        messagesService = new AppMessagesService(socketManager);
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
        Subscription connectSub = socketManager.on(Socket.EVENT_CONNECT)
                .subscribe($ -> {
                    Log.d(TAG, "Connected");
                    if (token != null) {
                        connectionService.authenticate(token);
                    }
                });

        Subscription disconnectSub = socketManager.on(Socket.EVENT_DISCONNECT)
                .subscribe($ -> {
                    Log.d(TAG, "Disconnected");
                    if (networkStateReceiver.isConnected()) {
                        socketManager.connect();
                    }
                });

        subscriptions.add(connectSub);
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

    public void reconnect() {
        socketManager.connect();
    }
}
