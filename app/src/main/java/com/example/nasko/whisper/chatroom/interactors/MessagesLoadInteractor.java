package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.BaseInteractor;
import com.example.nasko.whisper.models.view.MessageViewModel;

import java.util.List;

import rx.Observable;

public interface MessagesLoadInteractor extends BaseInteractor {

    Observable<List<MessageViewModel>> onMessagesLoaded();

    void loadMessages(int startSeq, int count);

    boolean isLoading();
}
