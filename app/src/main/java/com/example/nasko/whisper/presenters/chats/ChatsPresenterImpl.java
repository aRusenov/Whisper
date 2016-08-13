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
import com.example.nasko.whisper.presenters.SocketServicePresenter;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatsPresenterImpl extends SocketServicePresenter<ChatsView> implements ChatsPresenter {

    private static final String TAG = ChatsPresenterImpl.class.getName();

    private UserProvider userProvider;
    private Navigator navigator;

    private Subscription authSub;
    private Subscription loadChatsSub;
    private Subscription newChatSub;
    private Subscription updateChatSub;

    public ChatsPresenterImpl() {
        this(WhisperApplication.instance().getServiceConsumer(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getNavigator());
    }

    public ChatsPresenterImpl(SocketServiceBinder serviceBinder,
                              UserProvider userProvider,
                              Navigator navigator) {
        super(serviceBinder);
        this.userProvider = userProvider;
        this.navigator = navigator;
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        service.contactsService().loadContacts();

        authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    service.contactsService().loadContacts();
                });

        loadChatsSub = service.contactsService()
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

        updateChatSub = service.contactsService()
                .onChatUpdate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    if (view != null) {
                        view.updateChat(chat);
                    }
                });

        newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    if (view != null) {
                        setOtherContact(chat);
                        view.addChat(chat);
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();

        authSub.unsubscribe();
        loadChatsSub.unsubscribe();
        newChatSub.unsubscribe();
        updateChatSub.unsubscribe();
    }

    @Override
    public void onChatClicked(Chat clickedChat) {
        navigator.navigateToChatroom(
                context,
                userProvider.getCurrentUser(),
                clickedChat);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (service != null) {
            service.contactsService().loadContacts();
        }
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
}
