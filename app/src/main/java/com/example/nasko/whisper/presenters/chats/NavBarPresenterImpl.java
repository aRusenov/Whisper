package com.example.nasko.whisper.presenters.chats;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.JsonDeserializer;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.views.contracts.ChatsNavBarView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NavBarPresenterImpl extends ServiceBoundPresenter<ChatsNavBarView> implements NavBarPresenter {

    private static final String TAG = NavBarPresenterImpl.class.getName();

    private LocalUserRepository localUserRepository;
    private UserProvider userProvider;
    private Navigator navigator;
    private JsonDeserializer deserializer = new JsonDeserializer();

    public NavBarPresenterImpl() {
        this(WhisperApplication.instance().getLocalUserRepository(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getNavigator(),
                WhisperApplication.instance().getServiceBinder());
    }

    public NavBarPresenterImpl(LocalUserRepository localUserRepository,
                               UserProvider userProvider,
                               Navigator navigator,
                               SocketServiceBinder socketServiceBinder) {
        super(socketServiceBinder);
        this.localUserRepository = localUserRepository;
        this.userProvider = userProvider;
        this.navigator = navigator;
    }

    @Override
    public void attachView(ChatsNavBarView view, Context context, Bundle extras) {
        super.attachView(view, context, extras);
        User currentUser = userProvider.getCurrentUser();
        if (currentUser == null) {
            User loggedUser = localUserRepository.getLoggedUser();
            if (loggedUser.getSessionToken() == null) {
                logout();
                return;
            }

            userProvider.setCurrentUser(loggedUser);
        }
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);

        Subscription connectSub = service.connectionService()
                .onConnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Connected");
                    view.setNetworkStatus("Authenticating...");
                });

        Subscription authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Authenticated");
                    view.setNetworkStatus("Whisper");
                });

        Subscription disconnectSub = service.connectionService()
                .onDisconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    Log.d(TAG, "Disconnect");
                    view.setNetworkStatus("Connecting..");
                });

        subscriptions.add(connectSub);
        subscriptions.add(authSub);
        subscriptions.add(disconnectSub);
    }

    @Override
    public void onResume() {
        User currentUser = userProvider.getCurrentUser();
        serviceBinder.resume();
        if (currentUser == null) {
            return;
        }

        if (service == null) {
            serviceBinder.start(currentUser.getSessionToken());
        }
    }

    @Override
    public void onPause() {
        serviceBinder.pause();
    }

    @Override
    public void onSettingsClicked() {
        User currentUser = userProvider.getCurrentUser();
        if (currentUser != null) {
            navigator.navigateToProfileScreen(context, currentUser);
        }
    }

    @Override
    public void onLogoutClicked() {
        logout();
    }

    private void logout() {
        LocalUserRepository localUserRepository = new LocalUserRepository(context);
        localUserRepository.logout();
        serviceBinder.stop(true);
        navigator.navigateToLoginScreen(context);
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Presenter detached");
    }
}
