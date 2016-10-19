package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractor;
import com.example.nasko.whisper.models.view.ChatViewModel;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatsPresenter implements ChatsContract.Presenter {

    private ChatsContract.View view;
    private ViewCoordinator viewCoordinator;
    private CompositeSubscription subscriptions;

    private ChatsInteractor chatsInteractor;
    private ContactsStateInteractor contactsStateInteractor;

    public ChatsPresenter(ChatsContract.View view, ViewCoordinator viewCoordinator,
                          ChatsInteractor chatsInteractor, ContactsStateInteractor contactsStateInteractor) {
        this.view = view;
        this.viewCoordinator = viewCoordinator;
        this.chatsInteractor = chatsInteractor;
        this.contactsStateInteractor = contactsStateInteractor;

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() {
        chatsInteractor.loadChatsIfAuthenticated();

        Subscription loadChatsSub = chatsInteractor.onChatsLoaded()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chats -> {
                    view.clearChats();
                    view.loadChats(chats);
                });

        subscriptions.add(loadChatsSub);

        Subscription newChatSub = chatsInteractor.onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    view.addChat(chat);
                });

        subscriptions.add(newChatSub);

        Subscription newMessageSub = chatsInteractor.onChatNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.updateChatLastMessage(message.getChatId(), message);
                });

        subscriptions.add(newMessageSub);

        Subscription userOnlineSub = contactsStateInteractor.onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), true);
                });

        subscriptions.add(userOnlineSub);

        Subscription userOfflineSub = contactsStateInteractor.onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), false);
                });

        subscriptions.add(userOfflineSub);

        chatsInteractor.init();
        contactsStateInteractor.init();
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        chatsInteractor.destroy();
        contactsStateInteractor.destroy();
        subscriptions.clear();
        view = null;
    }

    @Override
    public void onChatClicked(ChatViewModel clickedChat) {
        viewCoordinator.onChatItemClicked(clickedChat);
    }
}
