package com.example.nasko.whisper.presenters.chatroom;

import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ChatroomView;

public interface ChatroomPresenter extends Presenter<ChatroomView> {

    void onMessageSend(String text, long identifier);

    void setLastLoadedMessageId(int lastLoadedMessageId);

    int getLastLoadedMessageId();

    boolean onScrollToTop();

    void onStartTyping();

    void onStopTyping();

    void onChatDisplayRequested(ChatViewModel chat);

    void onChatClosed();
}
