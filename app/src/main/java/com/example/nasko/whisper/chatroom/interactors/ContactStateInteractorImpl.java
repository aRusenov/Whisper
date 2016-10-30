package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.dto.ContactStateChange;
import com.example.nasko.whisper.models.view.ChatViewModel;

import rx.Observable;

public class ContactStateInteractorImpl implements ContactStateInteractor {

    private SocketService socketService;
    private ChatViewModel chat;

    public ContactStateInteractorImpl(SocketService socketService, ChatViewModel chat) {
        this.socketService = socketService;
        this.chat = chat;
    }

    @Override
    public Observable<ContactStateChange> onStateChanged() {
        return Observable.merge(
                    socketService.contactsService().onContactOnline(),
                    socketService.contactsService().onContactOffline())
                .filter(stateChange -> stateChange.getChatId().equals(chat.getId()));
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
