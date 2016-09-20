package com.example.nasko.whisper.presenters.chatroom;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.views.contracts.ChatroomNavBarView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomNavBarPresenter extends ServiceBoundPresenter<ChatroomNavBarView> {

    protected ChatroomNavBarPresenter(SocketServiceBinder binder) {
        super(binder);
    }

    public ChatroomNavBarPresenter() {
        this(WhisperApplication.instance().getServiceBinder());
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);

        Subscription userOnlineSub = service.contactsService()
                .onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    if (view != null) {
                        view.setContactStatus(true);
                    }
                });

        Subscription userOfflineSub = service.contactsService()
                .onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    if (view != null) {
                        view.setContactStatus(false);
                    }
                });

        subscriptions.add(userOnlineSub);
        subscriptions.add(userOfflineSub);
    }
}
