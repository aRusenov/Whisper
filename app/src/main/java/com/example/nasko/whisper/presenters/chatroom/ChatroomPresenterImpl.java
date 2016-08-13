package com.example.nasko.whisper.presenters.chatroom;

import android.content.Context;
import android.os.Bundle;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.SocketServicePresenter;
import com.example.nasko.whisper.views.contracts.ChatroomView;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomPresenterImpl extends SocketServicePresenter<ChatroomView> implements ChatroomPresenter {

    private static final int PAGE_SIZE = 10;
    private static final int DEFAULT_MESSAGE_SEQ = -1;
    public static final String LAST_MESSAGE_SEQ = "lastMessageSeq";

    private Subscription authSub;
    private Subscription newMsgSub;
    private Subscription loadMessagesSub;

    private Chat chat;
    private int lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
    private boolean loadingMessages;

    public ChatroomPresenterImpl(SocketServiceBinder serviceBinder) {
        super(serviceBinder);
    }

    public ChatroomPresenterImpl() {
        this(WhisperApplication.instance().getServiceConsumer());
    }

    @Override
    public void attachView(ChatroomView view, Context context, Bundle extras) {
        chat = extras.getParcelable("chat");
        super.attachView(view, context, extras);
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
            loadMesages();
        }

        authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
                        loadMesages();
                    }
                });

        loadMessagesSub = service.messageService()
                .onLoadMessages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadingMessages = false;
                    if (!response.getChatId().equals(chat.getId())) {
                        // For another chat room
                        return;
                    }

                    List<Message> messages = response.getMessages();
                    if (messages.size() > 0) {
                        int messageSeq = messages.get(0).getSeq();
                        lastLoadedMessageSeq = messageSeq;
                        if (view != null) {
                            view.loadMessages(messages);
                        }
                    }
                });

        newMsgSub = service.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (message.getChatId().equals(chat.getId())) {
                        if (view != null) {
                            view.addMessage(message);
                        }
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;

        authSub.unsubscribe();
        loadMessagesSub.unsubscribe();
        newMsgSub.unsubscribe();
    }

    @Override
    public void onMessageSend(String text) {
        service.messageService()
                .sendMessage(chat.getId(), text);
    }

    @Override
    public void onScrollToTop() {
        if (lastLoadedMessageSeq == 0) {
            return;
        }

        if (!loadingMessages) {
            loadMesages();
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
        super.onPause();
        serviceBinder.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceBinder.resume();
    }

    private void loadMesages() {
        service.messageService()
                .loadMessages(chat.getId(), lastLoadedMessageSeq, PAGE_SIZE * 2);
        loadingMessages = true;
    }
}
