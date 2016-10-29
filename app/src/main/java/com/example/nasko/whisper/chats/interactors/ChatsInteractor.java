package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;

import java.util.List;

import rx.Observable;

public interface ChatsInteractor extends BaseInteractor {

    Observable<List<ChatViewModel>> onChatsLoaded();

    Observable<ChatViewModel> onNewChat();

    Observable<MessageViewModel> onChatNewMessage();
}
