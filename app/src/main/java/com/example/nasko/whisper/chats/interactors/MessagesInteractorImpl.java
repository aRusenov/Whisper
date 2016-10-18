package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import rx.Observable;

public class MessagesInteractorImpl implements MessagesInteractor {

    private SocketService socketService;

    public MessagesInteractorImpl(SocketService socketService) {
        this.socketService = socketService;
    }

    @Override
    public Observable<MessageViewModel> onNewMessage() {
        return socketService.messageService().onNewMessage()
                .map(Mapper::toMessageViewModel);
    }

    @Override
    public Observable<MessageSentAck> onMessageSent() {
        return socketService.messageService().onMessageSent();
    }
}
