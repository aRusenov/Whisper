package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public class ContactsInteractorImpl implements ContactsInteractor {

    private User currentUser;
    private SocketService socketService;

    public ContactsInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        currentUser = userProvider.getCurrentUser();
    }

    @Override
    public Observable<ContactStateChange> onUserOnline() {
        return socketService.contactsService()
                .onContactOnline()
                .filter(stateChange -> ! stateChange.getUsername().equals(currentUser.getUsername()));
    }

    @Override
    public Observable<ContactStateChange> onUserOffline() {
        return socketService.contactsService()
                .onContactOffline()
                .filter(stateChange -> ! stateChange.getUsername().equals(currentUser.getUsername()));
    }
}
