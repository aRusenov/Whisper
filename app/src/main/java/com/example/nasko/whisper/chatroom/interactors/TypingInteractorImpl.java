package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;

import rx.Observable;

public class TypingInteractorImpl implements TypingInteractor {

    private SocketService socketService;
    private ContactViewModel userContact;
    private ChatViewModel chat;

    public TypingInteractorImpl(SocketService socketService, ContactViewModel userContact, ChatViewModel chat) {
        this.socketService = socketService;
        this.userContact = userContact;
        this.chat = chat;
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }

    @Override
    public Observable<TypingEvent> onTypingStart() {
        return socketService.messageService()
                .onStartTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()));
    }

    @Override
    public Observable<TypingEvent> onTypingEnd() {
        return socketService.messageService()
                .onStopTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()));
    }

    @Override
    public void startTyping() {
        socketService.messageService().startTyping(chat.getId(), userContact.getUsername());
    }

    @Override
    public void endTyping() {
        socketService.messageService().stopTyping(chat.getId(), userContact.getUsername());
    }
}
