package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends SocketPresenter implements ToolbarContract.Presenter {

    private ToolbarContract.View view;

    public ToolbarPresenter(ToolbarContract.View view, SocketService socketService, UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
    }

    @Override
    public void init() {
        Subscription userOnlineSub = socketService.contactsService()
                .onContactOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setContactStatus(true);
                });

        subscriptions.add(userOnlineSub);

        Subscription userOfflineSub = socketService.contactsService()
                .onContactOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setContactStatus(false);
                });

        subscriptions.add(userOfflineSub);
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }
}
