package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.models.view.ChatViewModel;

import java.util.List;

import rx.Observable;

public interface ChatsInteractor {

    Observable<List<ChatViewModel>> onChatsLoaded();

    Observable<ChatViewModel> onNewChat();

    void loadChats();
}
