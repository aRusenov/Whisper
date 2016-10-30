package com.example.nasko.whisper.chatroom.interactors;

import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;

import rx.Observable;

public class MessagesLoadInteractorImpl implements MessagesLoadInteractor {

    private SocketService socketService;
    private ChatViewModel chat;
    private boolean loading;

    public MessagesLoadInteractorImpl(SocketService socketService, ChatViewModel chat) {
        this.socketService = socketService;
        this.chat = chat;
    }

    @Override
    public Observable<List<MessageViewModel>> onMessagesLoaded() {
        return socketService.messageService()
                .onLoadMessages()
                .filter(response -> response.getChatId().equals(chat.getId()))
                .map(response -> Mapper.toMessageViewModelList(response.getMessages()))
                .doOnNext(messages -> {
                    loading = false;
                });
    }

    @Override
    public void loadMessages(int startSeq, int count) {
        if (!loading && socketService.authenticated()) {
            // TODO: This could hang if connection dies and loading remains true. Add timeout.
            socketService.messageService().loadMessages(chat.getId(), startSeq, count);
            loading = true;
        }
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }
}
