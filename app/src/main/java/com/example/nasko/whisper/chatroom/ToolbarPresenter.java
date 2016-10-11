package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.ServiceBoundPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ToolbarPresenter extends ServiceBoundPresenter implements ToolbarContract.Presenter {

    private ToolbarContract.View view;

    public ToolbarPresenter(ToolbarContract.View view, SocketServiceBinder binder, UserProvider userProvider) {
        super(binder, userProvider);
        this.view = view;
    }

    @Override
    public void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions) {
        Subscription userOnlineSub = service.contactsService()
                .onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setContactStatus(true);
                });

        serviceSubscriptions.add(userOnlineSub);

        Subscription userOfflineSub = service.contactsService()
                .onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setContactStatus(false);
                });

        serviceSubscriptions.add(userOfflineSub);
    }

    @Override
    public void onServiceUnbind() { }
}
