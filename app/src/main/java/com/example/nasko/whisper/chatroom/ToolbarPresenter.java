package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.chatroom.interactors.ContactStateInteractor;

import rx.android.schedulers.AndroidSchedulers;

public class ToolbarPresenter extends AbstractPresenter<ToolbarContract.View> implements ToolbarContract.Presenter {

    private ContactStateInteractor contactStateInteractor;

    public ToolbarPresenter(ToolbarContract.View view, ContactStateInteractor contactStateInteractor) {
        super(view);
        this.contactStateInteractor = contactStateInteractor;
    }

    @Override
    public void init() {
        subscriptions.add(contactStateInteractor.onStateChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    boolean online = stateChange.getState().equals("online");
                    view.setContactStatus(online);
                }));

        contactStateInteractor.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        contactStateInteractor.destroy();
    }
}
