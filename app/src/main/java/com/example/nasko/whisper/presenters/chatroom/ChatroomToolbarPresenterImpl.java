package com.example.nasko.whisper.presenters.chatroom;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.views.contracts.ChatroomNavBarView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomToolbarPresenterImpl extends ServiceBoundPresenter<ChatroomNavBarView> implements ChatroomToolbarPresenter {

    protected ChatroomToolbarPresenterImpl(SocketServiceBinder binder) {
        super(binder);
    }

    public ChatroomToolbarPresenterImpl() {
        this(WhisperApplication.instance().getServiceBinder());
    }

    @Override
    public void updateContactStatus(ContactViewModel contact) {
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
