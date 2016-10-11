package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.helpers.Mapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatsPresenter extends ServiceBoundPresenter implements ChatsContract.Presenter {

    private static final String TAG = ChatsPresenter.class.getName();

    private ChatsContract.View view;
    private ViewCoordinator viewCoordinator;

    public ChatsPresenter(ChatsContract.View view, ViewCoordinator viewCoordinator,
                          SocketServiceBinder serviceBinder, UserProvider userProvider) {
        super(serviceBinder, userProvider);
        this.view = view;
        this.viewCoordinator = viewCoordinator;
    }

    @Override
    public void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions) {
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

                    List<ChatViewModel> chatViewModels = Mapper.toChatViewModelList(chatsArr);
                    for (int i = 0; i < chatViewModels.size(); i++) {
                        ChatViewModel chatViewModel = chatViewModels.get(i);
                        List<Contact> chatParticipants = chatsArr[i].getParticipants();
                        setDisplayContact(chatViewModel, chatParticipants);
                    }

                    view.clearChats();
                    view.loadChats(chatViewModels);
                });

        Subscription newMsgSub = service.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.updateChatLastMessage(
                            message.getChatId(),
                            Mapper.toMessageViewModel(message));
                });

        Subscription messageSentSub = service.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    view.updateChatLastMessage(
                            messageSentAck.getChatId(),
                            Mapper.toMessageViewModel(messageSentAck.getMessage()));
                });

        Subscription userOnlineSub = service.contactsService()
                .onUserOnline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), true);
                });

        Subscription userOfflineSub = service.contactsService()
                .onUserOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), false);
                });

        Subscription newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    ChatViewModel chatViewModel = Mapper.toChatViewModel(chat);
                    setDisplayContact(chatViewModel, chat.getParticipants());

                    view.addChat(chatViewModel);
                });

        serviceSubscriptions.add(authSub);
        serviceSubscriptions.add(newMsgSub);
        serviceSubscriptions.add(messageSentSub);
        serviceSubscriptions.add(newChatSub);
        serviceSubscriptions.add(loadChatsSub);
        serviceSubscriptions.add(userOnlineSub);
        serviceSubscriptions.add(userOfflineSub);
    }

    @Override
    public void onServiceUnbind() { }

    @Override
    public void onChatClicked(ChatViewModel clickedChat) {
        viewCoordinator.onChatItemClicked(clickedChat);
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
}
