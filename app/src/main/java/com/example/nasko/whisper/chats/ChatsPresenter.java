package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsInteractor;
import com.example.nasko.whisper.chats.interactors.MessagesInteractor;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatsPresenter implements ChatsContract.Presenter {

    private ChatsContract.View view;
    private ViewCoordinator viewCoordinator;
    private CompositeSubscription subscriptions;

    private ChatsInteractor chatsInteractor;
    private ConnectionInteractor connectionInteractor;
    private MessagesInteractor messagesInteractor;
    private ContactsInteractor contactsInteractor;

    public ChatsPresenter(ChatsContract.View view, ViewCoordinator viewCoordinator,
                          ChatsInteractor chatsInteractor, ConnectionInteractor connectionInteractor,
                          MessagesInteractor messagesInteractor, ContactsInteractor contactsInteractor) {
        this.view = view;
        this.viewCoordinator = viewCoordinator;
        this.chatsInteractor = chatsInteractor;
        this.connectionInteractor = connectionInteractor;
        this.messagesInteractor = messagesInteractor;
        this.contactsInteractor = contactsInteractor;

        subscriptions = new CompositeSubscription();
        initListeners();
    }

    private void initListeners() {
        Subscription authSub = connectionInteractor.onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    chatsInteractor.loadChats();
                });

        subscriptions.add(authSub);

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

        Subscription newMsgSub = messagesInteractor.onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.updateChatLastMessage(message.getChatId(), message);
                });

        subscriptions.add(newMsgSub);

        Subscription messageSentSub = messagesInteractor.onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    view.updateChatLastMessage(
                            messageSentAck.getChatId(),
                            Mapper.toMessageViewModel(messageSentAck.getMessage()));
                });

        subscriptions.add(messageSentSub);

        Subscription userOnlineSub = contactsInteractor.onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), true);
                });

        subscriptions.add(userOnlineSub);

        Subscription userOfflineSub = contactsInteractor.onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), false);
                });

        subscriptions.add(userOfflineSub);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        subscriptions.clear();
        view = null;
    }

    @Override
    public void onChatClicked(ChatViewModel clickedChat) {
        viewCoordinator.onChatItemClicked(clickedChat);
    }
}
