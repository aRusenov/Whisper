package com.example.nasko.whisper.presenters.chats;

import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatsPresenterImpl extends ServiceBoundPresenter<ChatsView> implements ChatsPresenter {

    private static final String TAG = ChatsPresenterImpl.class.getName();

    private UserProvider userProvider;
    private Navigator navigator;

    public ChatsPresenterImpl() {
        this(WhisperApplication.instance().getServiceBinder(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getNavigator());
    }

    public ChatsPresenterImpl(SocketServiceBinder serviceBinder,
                              UserProvider userProvider,
                              Navigator navigator) {
        super(serviceBinder);
        this.userProvider = userProvider;
        this.navigator = navigator;
        FirebaseMessaging.getInstance().subscribeToTopic("chats");
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        service.contactsService().loadContacts();

        Subscription authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    service.contactsService().loadContacts();
                });

        Subscription loadChatsSub = service.contactsService()
                .onLoadChats()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatsArr -> {
                    Log.d(TAG, "Loading fresh chats");
                    if (view != null) {
                        List<Chat> chats = Arrays.asList(chatsArr);
                        for (int i = 0; i < chats.size(); i++) {
                            setOtherContact(chats.get(i));
                        }

                        view.clearChats();
                        view.loadChats(chats);
                    }
                });

        Subscription updateChatSub = service.contactsService()
                .onChatUpdate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    if (view != null) {
                        view.updateChat(chat);
                    }
                });

        Subscription newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    setOtherContact(chat);
                    if (view != null) {
                        view.addChat(chat);
                    }
                });

        subscriptions.add(authSub);
        subscriptions.add(loadChatsSub);
        subscriptions.add(updateChatSub);
        subscriptions.add(newChatSub);
    }

    @Override
    public void onChatClicked(Chat clickedChat) {
        navigator.navigateToChatroom(
                context,
                userProvider.getCurrentUser(),
                clickedChat);
    }

    private void setOtherContact(Chat chat) {
        User currentUser = userProvider.getCurrentUser();
        List<Contact> participants = chat.getParticipants();
        int i;
        for (i = 0; i < participants.size(); i++) {
            String participantId = participants.get(i).getId();
            if (! participantId.equals(currentUser.getUId())) {
                break;
            }
        }

        chat.setOtherContact(participants.get(i));
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Presenter detached");
    }
}
