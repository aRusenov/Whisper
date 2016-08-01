package com.example.nasko.whisper.presenters;

import android.os.Bundle;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessagesQueryResponse;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceConsumer;
import com.example.nasko.whisper.views.contracts.ChatroomView;

import java.util.List;

public class ChatroomPresenterImpl implements ChatroomPresenter {

    private static final int PAGE_SIZE = 10;
    public static final String LAST_MESSAGE_SEQ = "lastMessageSeq";

    private ChatroomView chatroomView;

    private SocketServiceConsumer serviceReceiver;
    private String chatId;
    private int lastLoadedMessageSeq = -1;
    private boolean loadingMessages;

    public ChatroomPresenterImpl() {
        serviceReceiver = WhisperApplication.instance().getServiceReceiver();
        registerEventListeners();
    }

    private void registerEventListeners() {
        serviceReceiver.setMessagesEventListener(new MessagesEventListener() {
            @Override
            public void onMessageAdded(Message message) {
                if (message.getChatId().equals(chatId)) {
                    chatroomView.addMessage(message);
                }
            }

            @Override
            public void onMessagesLoaded(MessagesQueryResponse response) {
                loadingMessages = false;
                if (! response.getChatId().equals(chatId)) {
                    // For another chat room
                    return;
                }

                List<Message> messages = response.getMessages();
                if (messages.size() > 0) {
                    int messageSeq = messages.get(0).getSeq();
                    lastLoadedMessageSeq = messageSeq;

                    chatroomView.loadMessages(messages);
                }
            }
        });
    }

    @Override
    public void onTakeChatroomView(ChatroomView view, String chatId) {
        chatroomView = view;
        this.chatId = chatId;
        serviceReceiver.loadMessages(chatId, lastLoadedMessageSeq, PAGE_SIZE * 2);
        loadingMessages = true;
    }

    @Override
    public void onMessageSend(String text) {
        serviceReceiver.sendMessage(text, chatId);
    }

    @Override
    public void onScrollToTop() {
        if (lastLoadedMessageSeq == 0) {
            return;
        }

        if (!loadingMessages) {
            serviceReceiver.loadMessages(chatId, lastLoadedMessageSeq, PAGE_SIZE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        outBundle.putInt(LAST_MESSAGE_SEQ, lastLoadedMessageSeq);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(LAST_MESSAGE_SEQ)) {
            lastLoadedMessageSeq = savedInstanceState.getInt(LAST_MESSAGE_SEQ);
        }
    }

    @Override
    public void onPause() {
        serviceReceiver.pause();
    }

    @Override
    public void onResume() {
        serviceReceiver.resume();
    }

    @Override
    public void onDestroy() {
        lastLoadedMessageSeq = -1;
    }
}
