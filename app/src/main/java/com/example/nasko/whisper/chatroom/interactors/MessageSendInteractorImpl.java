package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.Date;

import rx.Observable;

public class MessageSendInteractorImpl implements MessageSendInteractor {

    private SocketService socketService;
    private ChatViewModel chat;
    private ContactViewModel userContact;

    public MessageSendInteractorImpl(SocketService socketService, ChatViewModel chat, ContactViewModel userContact) {
        this.socketService = socketService;
        this.chat = chat;
        this.userContact = userContact;
    }

    @Override
    public Observable<MessageViewModel> onNewMessage() {
        return socketService.messageService()
                .onNewMessage()
                .filter(message -> message.getChatId().equals(chat.getId()))
                .map(Mapper::toMessageViewModel);
    }

    @Override
    public Observable<MessageSentAck> onMessageSent() {
        return socketService.messageService()
                .onMessageSent()
                .filter(msgSentAck -> msgSentAck.getChatId().equals(chat.getId()));
    }

    @Override
    public MessageViewModel prepareMessage(String text) {
        MessageViewModel newMessage = new MessageViewModel(chat.getId(), text.trim(), new Date(), userContact);
        newMessage.setStatus(MessageStatus.PENDING);
        newMessage.setUId(System.nanoTime()); // Used to identify when the server returns an Ack
        return newMessage;
    }

    @Override
    public void sendMessage(MessageViewModel message) {
        socketService.messageService().sendMessage(chat.getId(), message.getText(), message.getIdentifier());
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
