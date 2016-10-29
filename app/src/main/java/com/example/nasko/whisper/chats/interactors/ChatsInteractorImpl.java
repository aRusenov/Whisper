package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;

import rx.Observable;
import rx.Subscription;

public class ChatsInteractorImpl implements ChatsInteractor {

    private Subscription authSub;
    private SocketService socketService;
    private User currentUser;

    public ChatsInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        this.currentUser = userProvider.getCurrentUser();
    }

    @Override
    public void init() {
        loadChatsIfAuthenticated();
        authSub = socketService.connectionService()
                .onAuthenticated()
                .subscribe($ -> loadChatsIfAuthenticated());
    }

    @Override
    public void destroy() {
        authSub.unsubscribe();
    }

    @Override
    public Observable<List<ChatViewModel>> onChatsLoaded() {
        return socketService.contactsService()
                .onLoadChats()
                .map(chats -> {
                    List<ChatViewModel> chatViewModels = Mapper.toChatViewModelList(chats);
                    for (int i = 0; i < chatViewModels.size(); i++) {
                        ChatViewModel chatViewModel = chatViewModels.get(i);
                        List<Contact> chatParticipants = chats[i].getParticipants();
                        setDisplayContact(chatViewModel, chatParticipants);
                    }

                    return chatViewModels;
                });
    }

    @Override
    public Observable<ChatViewModel> onNewChat() {
        return socketService.contactsService()
                .onNewChat()
                .map(chat -> {
                    ChatViewModel chatViewModel = Mapper.toChatViewModel(chat);
                    setDisplayContact(chatViewModel, chat.getParticipants());

                    return chatViewModel;
                });
    }

    @Override
    public Observable<MessageViewModel> onChatNewMessage() {
        return Observable.merge(
                socketService.messageService().onNewMessage()
                        .map(Mapper::toMessageViewModel),
                socketService.messageService().onMessageSent()
                        .map(messageSentAck -> Mapper.toMessageViewModel(messageSentAck.getMessage()))
        );
    }

    private void loadChatsIfAuthenticated() {
        if (socketService.authenticated()) {
            socketService.contactsService().loadContacts();
        }
    }

    private void setDisplayContact(ChatViewModel chat, List<Contact> participants) {
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
