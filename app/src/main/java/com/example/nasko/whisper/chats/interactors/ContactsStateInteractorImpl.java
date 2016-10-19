package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.ContactStateChange;

import rx.Observable;

public class ContactsStateInteractorImpl implements ContactsStateInteractor {

    private SocketService socketService;
    private User currentUser;

    public ContactsStateInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        currentUser = userProvider.getCurrentUser();
        if (currentUser != null && currentUser.getSessionToken() != null) {
            socketService.start(currentUser.getSessionToken());
        }
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

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
