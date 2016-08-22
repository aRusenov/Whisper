package com.example.nasko.whisper.presenters.chatroom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.MessageNotificationController;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.views.contracts.ChatroomView;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomPresenterImpl extends ServiceBoundPresenter<ChatroomView> implements ChatroomPresenter {

    private static final String TAG = ChatroomPresenter.class.getName();
    private static final int PAGE_SIZE = 10;
    private static final int DEFAULT_MESSAGE_SEQ = -1;
    private static final String LAST_MESSAGE_SEQ = "lastMessageSeq";

    private Chat chat;
    private int lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
    private boolean loadingMessages;

    private MessageNotificationController notificationController;

    public ChatroomPresenterImpl(SocketServiceBinder serviceBinder, MessageNotificationController notificationController) {
        super(serviceBinder);
        this.notificationController = notificationController;
    }

    public ChatroomPresenterImpl() {
        this(WhisperApplication.instance().getServiceConsumer(),
                WhisperApplication.instance().getNotificationController());
    }

    @Override
    public void attachView(ChatroomView view, Context context, Bundle extras) {
        chat = extras.getParcelable("chat");
        notificationController.removeNotification(chat.getOtherContact().getId());
        super.attachView(view, context, extras);
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
            loadMesages();
        }

        Subscription authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
                        loadMesages();
                    }
                });

        Subscription loadMessagesSub = service.messageService()
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

        Subscription newMsgSub = service.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (message.getChatId().equals(chat.getId())) {
                        if (view != null) {
                            view.addMessage(message);
                        }
                    }
                });

        subscriptions.add(authSub);
        subscriptions.add(loadMessagesSub);
        subscriptions.add(newMsgSub);
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Detaching presenter");
        lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
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
