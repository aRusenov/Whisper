package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractor;
import com.example.nasko.whisper.models.view.ChatViewModel;

import rx.android.schedulers.AndroidSchedulers;

public class ChatsPresenter extends AbstractPresenter<ChatsContract.View> implements ChatsContract.Presenter {

    private ViewCoordinator viewCoordinator;
    private ChatsInteractor chatsInteractor;
    private ContactsStateInteractor contactsStateInteractor;

    public ChatsPresenter(ChatsContract.View view, ViewCoordinator viewCoordinator,
                          ChatsInteractor chatsInteractor, ContactsStateInteractor contactsStateInteractor) {
        super(view);
        this.viewCoordinator = viewCoordinator;
        this.chatsInteractor = chatsInteractor;
        this.contactsStateInteractor = contactsStateInteractor;
    }

    @Override
    public void init() {
        subscriptions.add(chatsInteractor.onChatsLoaded()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chats -> {
                    view.clearChats();
                    view.loadChats(chats);
                }));

        subscriptions.add(chatsInteractor.onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    view.addChat(chat);
                }));

        subscriptions.add(chatsInteractor.onChatNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.updateChatLastMessage(message.getChatId(), message);
                }));

        subscriptions.add(contactsStateInteractor.onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), true);
                }));

        subscriptions.add(contactsStateInteractor.onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), false);
                }));

        chatsInteractor.init();
        contactsStateInteractor.init();
    }

    @Override
    public void destroy() {
        super.destroy();
        chatsInteractor.destroy();
        contactsStateInteractor.destroy();
    }

    @Override
    public void onChatClicked(ChatViewModel clickedChat) {
        viewCoordinator.onChatItemClicked(clickedChat);
    }
}
