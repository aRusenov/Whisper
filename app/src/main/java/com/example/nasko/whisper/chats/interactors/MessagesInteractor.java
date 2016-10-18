package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.view.MessageViewModel;

import rx.Observable;

public interface MessagesInteractor {

    Observable<MessageViewModel> onNewMessage();

    Observable<MessageSentAck> onMessageSent();
}
