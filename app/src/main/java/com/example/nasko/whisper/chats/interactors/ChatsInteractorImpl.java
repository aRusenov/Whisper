package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;

import rx.Observable;

public class ChatsInteractorImpl implements ChatsInteractor {

    private SocketService socketService;
    private UserProvider userProvider;

    public ChatsInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        this.userProvider = userProvider;
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
    public void loadChats() {
        if (socketService.authenticated()) {
            socketService.contactsService().loadContacts();
        }
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
