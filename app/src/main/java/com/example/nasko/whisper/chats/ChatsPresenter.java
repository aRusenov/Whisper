package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatsPresenter extends SocketPresenter implements ChatsContract.Presenter {

    private static final String TAG = ChatsPresenter.class.getName();

    private ChatsContract.View view;
    private ViewCoordinator viewCoordinator;
    private User currentUser;

    public ChatsPresenter(ChatsContract.View view, ViewCoordinator viewCoordinator,
                          SocketService socketService, UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
        this.viewCoordinator = viewCoordinator;

        currentUser = userProvider.getCurrentUser();
        initListeners();
        socketService.contactsService().loadContacts();
    }

    private void initListeners() {
        Subscription authSub = socketService.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    socketService.contactsService().loadContacts();
                });

        Subscription loadChatsSub = socketService.contactsService()
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

        Subscription newMsgSub = socketService.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.updateChatLastMessage(
                            message.getChatId(),
                            Mapper.toMessageViewModel(message));
                });

        Subscription messageSentSub = socketService.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    view.updateChatLastMessage(
                            messageSentAck.getChatId(),
                            Mapper.toMessageViewModel(messageSentAck.getMessage()));
                });

        Subscription userOnlineSub = socketService.contactsService()
                .onContactOnline()
                .filter(stateChange -> ! stateChange.getUsername().equals(currentUser.getUsername()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), true);
                });

        Subscription userOfflineSub = socketService.contactsService()
                .onContactOffline()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateChange -> {
                    view.setChatStatus(stateChange.getChatId(), false);
                });

        Subscription newChatSub = socketService.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    ChatViewModel chatViewModel = Mapper.toChatViewModel(chat);
                    setDisplayContact(chatViewModel, chat.getParticipants());

                    view.addChat(chatViewModel);
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
    public void destroy() {
        super.destroy();
        view = null;
    }

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
