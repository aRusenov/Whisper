package com.example.nasko.whisper.presenters.chats;

import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.Mapper;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.google.firebase.messaging.FirebaseMessaging;

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
                        List<ChatViewModel> chatViewModels = Mapper.toChatViewModelList(chatsArr);
                        for (int i = 0; i < chatViewModels.size(); i++) {
                            ChatViewModel chatViewModel = chatViewModels.get(i);
                            List<Contact> chatParticipants = chatsArr[i].getParticipants();
                            setDisplayContact(chatViewModel, chatParticipants);
                        }

                        view.clearChats();
                        view.loadChats(chatViewModels);
                    }
                });

        Subscription newMsgSub = service.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (view != null) {
                        view.updateChatLastMessage(
                                message.getChatId(),
                                Mapper.toMessageViewModel(message));
                    }
                });

        Subscription messageSentSub = service.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    if (view != null) {
                        view.updateChatLastMessage(
                                messageSentAck.getChatId(),
                                Mapper.toMessageViewModel(messageSentAck.getMessage()));
                    }
                });

        Subscription userOnlineSub = service.contactsService()
                .onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    if (view != null) {
                        view.setChatStatus(stateChange.getChatId(), true);
                    }
                });

        Subscription userOfflineSub = service.contactsService()
                .onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    if (view != null) {
                        view.setChatStatus(stateChange.getChatId(), false);
                    }
                });

        Subscription newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                     if (view != null) {
                         ChatViewModel chatViewModel = Mapper.toChatViewModel(chat);
                         setDisplayContact(chatViewModel, chat.getParticipants());

                         view.addChat(chatViewModel);
                     }
                });

        subscriptions.add(authSub);
        subscriptions.add(newMsgSub);
        subscriptions.add(messageSentSub);
        subscriptions.add(newChatSub);
        subscriptions.add(loadChatsSub);
        subscriptions.add(userOnlineSub);
        subscriptions.add(userOfflineSub);
    }

    @Override
    public void onChatClicked(ChatViewModel clickedChat) {
        navigator.navigateToChatroom(
                context,
                userProvider.getCurrentUser(),
                clickedChat);
    }

    private void setDisplayContact(ChatViewModel chat, List<Contact> participants) {
        User currentUser = userProvider.getCurrentUser();
        int i;
        for (i = 0; i < participants.size(); i++) {
            String participantId = participants.get(i).getId();
            if (! participantId.equals(currentUser.getUId())) {
                break;
            }
        }

        chat.setDisplayContact(Mapper.toContactViewModel(participants.get(i)));
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Presenter detached");
    }
}
