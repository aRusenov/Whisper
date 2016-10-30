package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.view.MessageViewModel;

import rx.Observable;

public interface MessageSendInteractor extends BaseInteractor {

    Observable<MessageViewModel> onNewMessage();

    Observable<MessageSentAck> onMessageSent();

    MessageViewModel prepareMessage(String text);

    void sendMessage(MessageViewModel message);
}
