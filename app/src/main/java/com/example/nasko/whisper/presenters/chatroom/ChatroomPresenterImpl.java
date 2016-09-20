package com.example.nasko.whisper.presenters.chatroom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.MessageNotificationController;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.Mapper;
import com.example.nasko.whisper.views.contracts.ChatroomView;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomPresenterImpl extends ServiceBoundPresenter<ChatroomView> implements ChatroomPresenter {

    private static final String TAG = ChatroomPresenter.class.getName();
    private static final int PAGE_SIZE = 10;
    private static final int DEFAULT_MESSAGE_SEQ = -1;
    private static final String LAST_MESSAGE_SEQ = "lastMessageSeq";

    private ChatViewModel chat;
    private int lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
    private boolean loadingMessages;

    private MessageNotificationController notificationController;
    private UserProvider userProvider;

    public ChatroomPresenterImpl(SocketServiceBinder serviceBinder, MessageNotificationController notificationController, UserProvider userProvider) {
        super(serviceBinder);
        this.notificationController = notificationController;
        this.userProvider = userProvider;
    }

    public ChatroomPresenterImpl() {
        this(WhisperApplication.instance().getServiceBinder(),
                WhisperApplication.instance().getNotificationController(),
                WhisperApplication.instance().getUserProvider());
    }

    @Override
    public void attachView(ChatroomView view, Context context, Bundle extras) {
        chat = extras.getParcelable("chat");
        if (chat != null) {
            notificationController.removeNotification(chat.getDisplayContact().getId());
        }

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
                .filter(response -> response.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadingMessages = false;

                    List<Message> messages = response.getMessages();
                    if (messages.size() > 0) {
                        int messageSeq = messages.get(0).getSeq();
                        lastLoadedMessageSeq = messageSeq;
                        if (view != null) {
                            List<MessageViewModel> viewModelList = Mapper.toMessageViewModelList(messages);
                            view.loadMessages(viewModelList);
                        }
                    }
                });

        Subscription newMsgSub = service.messageService()
                .onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (message.getChatId().equals(chat.getId())) {
                        if (view != null) {
                            MessageViewModel msg = Mapper.toMessageViewModel(message);
                            view.addMessage(msg);
                        }
                    }
                });

        Subscription messageSentSub = service.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    if (view != null) {
                        view.updateMessageStatus(messageSentAck.getMessageIdentifier(), MessageStatus.SENT);
                    }
                });

        Subscription startTypingSub = service.messageService()
                .onStartTyping()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    if (typingEvent.getChatId().equals(chat.getId()) && view != null) {
                        view.displayTypingStarted(typingEvent);
                    }
                });

        Subscription stopTypingSub = service.messageService()
                .onStopTyping()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    if (typingEvent.getChatId().equals(chat.getId()) && view != null) {
                        view.displayTypingStopped(typingEvent);
                    }
                });

        subscriptions.add(authSub);
        subscriptions.add(loadMessagesSub);
        subscriptions.add(newMsgSub);
        subscriptions.add(messageSentSub);
        subscriptions.add(startTypingSub);
        subscriptions.add(stopTypingSub);
    }

    @Override
    public void detachView() {
        super.detachView();
        Log.d(TAG, "Detaching presenter");
        lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
    }

    @Override
    public void onMessageSend(String text, long msgIdentifier) {
        service.messageService()
                .sendMessage(chat.getId(), text.trim(), msgIdentifier);
    }

    @Override
    public boolean onScrollToTop() {
        if (lastLoadedMessageSeq == 0) {
            return false;
        }

        if (!loadingMessages) {
            loadMesages();
            return true;
        }

        return false;
    }

    @Override
    public void onStartTyping() {
        if (service != null) {
            service.messageService().startTyping(chat.getId(), userProvider.getCurrentUser().getUsername());
        }
    }

    @Override
    public void onStopTyping() {
        if (service != null) {
            service.messageService().stopTyping(chat.getId(), userProvider.getCurrentUser().getUsername());
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
    public void onResume() {
        super.onResume();

        User currentUser = userProvider.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        if (service == null) {
            serviceBinder.start(currentUser.getSessionToken());
        }
    }

    private void loadMesages() {
        service.messageService()
                .loadMessages(chat.getId(), lastLoadedMessageSeq, PAGE_SIZE * 2);
        loadingMessages = true;
    }
}
