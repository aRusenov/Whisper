package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.SessionInteractor;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ToolbarPresenter implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private ToolbarContract.View view;
    private SessionInteractor sessionInteractor;
    private ConnectionInteractor connectionInteractor;
    private CompositeSubscription subscriptions;

    public ToolbarPresenter(ToolbarContract.View view,
                            ConnectionInteractor connectionInteractor,
                            SessionInteractor sessionInteractor) {
        this.view = view;
        this.connectionInteractor = connectionInteractor;
        this.sessionInteractor = sessionInteractor;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() {
        sessionInteractor.init();
        connectionInteractor.init();

        subscriptions.add(connectionInteractor.onConnected()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    Log.d(TAG, "Connected");
                    view.setNetworkStatus(NetworkStatus.CONNECTED);
                }));

        subscriptions.add(connectionInteractor.onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    Log.d(TAG, "Authenticated");
                    view.setNetworkStatus(NetworkStatus.AUTHENTICATED);
                }));

        subscriptions.add(connectionInteractor.onConnectionProblem()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    view.setNetworkStatus(NetworkStatus.CONNECTING);
                }));
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        subscriptions.clear();
        connectionInteractor.destroy();
        sessionInteractor.destroy();
        view = null;
    }

    @Override
    public void onContactsClicked() {
        view.navigateToContacts();
    }

    @Override
    public void onSettingsClicked() {
        view.navigateToSettings();
    }

    @Override
    public void onLogoutClicked() {
        sessionInteractor.logoutUser();
        view.navigateToLoginScreen();
    }
}
