package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.SessionInteractor;

import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends AbstractPresenter<ToolbarContract.View> implements ToolbarContract.Presenter {

    private static final String TAG = "ToolbarPresenter";

    private SessionInteractor sessionInteractor;
    private ConnectionInteractor connectionInteractor;

    public ToolbarPresenter(ToolbarContract.View view,
                            ConnectionInteractor connectionInteractor,
                            SessionInteractor sessionInteractor) {
        super(view);
        this.connectionInteractor = connectionInteractor;
        this.sessionInteractor = sessionInteractor;
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
    public void destroy() {
        super.destroy();
        connectionInteractor.destroy();
        sessionInteractor.destroy();
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
