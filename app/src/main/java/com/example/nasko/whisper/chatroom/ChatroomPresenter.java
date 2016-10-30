package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.AbstractPresenter;
import com.example.nasko.whisper.chatroom.interactors.MessageSendInteractor;
import com.example.nasko.whisper.chatroom.interactors.MessagesLoadInteractor;
import com.example.nasko.whisper.chatroom.interactors.NotificationDismissInteractor;
import com.example.nasko.whisper.chatroom.interactors.TypingInteractor;
import com.example.nasko.whisper.chats.interactors.ConnectionInteractor;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;

import rx.android.schedulers.AndroidSchedulers;

public class ChatroomPresenter extends AbstractPresenter<ChatroomContract.View> implements ChatroomContract.Presenter {

    private static final int PAGE_SIZE = 10;

    private int lastLoadedMessageSeq;

    private NotificationDismissInteractor notificationDismissInteractor;
    private ConnectionInteractor connectionInteractor;
    private MessagesLoadInteractor messagesLoadInteractor;
    private MessageSendInteractor messageSendInteractor;
    private TypingInteractor typingInteractor;

    public ChatroomPresenter(ChatroomContract.View view, NotificationDismissInteractor notificationDismissInteractor,
                             ConnectionInteractor connectionInteractor, MessagesLoadInteractor messagesLoadInteractor,
                             MessageSendInteractor messageSendInteractor, TypingInteractor typingInteractor,
                             int lastLoadedMessageSeq) {
        super(view);
        this.notificationDismissInteractor = notificationDismissInteractor;
        this.connectionInteractor = connectionInteractor;
        this.messagesLoadInteractor = messagesLoadInteractor;
        this.messageSendInteractor = messageSendInteractor;
        this.typingInteractor = typingInteractor;
        this.lastLoadedMessageSeq = lastLoadedMessageSeq;
    }

    @Override
    public void init() {
        subscriptions.add(connectionInteractor.onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideNetworkError();
                    if (lastLoadedMessageSeq == -1) {
                        messagesLoadInteractor.loadMessages(lastLoadedMessageSeq, PAGE_SIZE);
                    }
                }));

        subscriptions.add(connectionInteractor.onConnectionProblem()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    view.displayNetworkError();
                }));

        subscriptions.add(messagesLoadInteractor.onMessagesLoaded()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messages -> {
                    if (messages.size() > 0) {
                        lastLoadedMessageSeq = messages.get(0).getSequentialId();
                    }

                    view.loadMessages(messages);
                }));

        subscriptions.add(messageSendInteractor.onNewMessage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    view.addMessage(message);
                }));

        subscriptions.add(messageSendInteractor.onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msgSentAck -> {
                    view.updateMessageStatus(msgSentAck.getMessageIdentifier(), MessageStatus.SENT);
                }));

        subscriptions.add(messageSendInteractor.onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msgSentAck -> {
                    view.updateMessageStatus(msgSentAck.getMessageIdentifier(), MessageStatus.SENT);
                }));

        subscriptions.add(typingInteractor.onTypingStart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStarted(typingEvent);
                }));

        subscriptions.add(typingInteractor.onTypingEnd()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStopped();
                }));

        connectionInteractor.init();
        messagesLoadInteractor.init();
        messageSendInteractor.init();
        typingInteractor.init();
        notificationDismissInteractor.init();

        if (lastLoadedMessageSeq == -1) {
            messagesLoadInteractor.loadMessages(-1, PAGE_SIZE * 2);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        connectionInteractor.destroy();
        messagesLoadInteractor.destroy();
        messageSendInteractor.destroy();
        typingInteractor.destroy();
        notificationDismissInteractor.destroy();
    }

    @Override
    public MessageViewModel onMessageSend(String text, ContactViewModel userContact) {
        MessageViewModel newMessage = messageSendInteractor.prepareMessage(text);
        messageSendInteractor.sendMessage(newMessage);
        return newMessage;
    }

    @Override
    public boolean onScrollToTop() {
        if (lastLoadedMessageSeq == 0) {
            return false;
        }

        if (! messagesLoadInteractor.isLoading()) {
            messagesLoadInteractor.loadMessages(lastLoadedMessageSeq, PAGE_SIZE);
            return true;
        }

        return false;
    }

    @Override
    public void onStartTyping() {
        typingInteractor.startTyping();
    }

    @Override
    public void onStopTyping() {
        typingInteractor.endTyping();
    }

    @Override
    public void setLastLoadedMessageId(int lastLoadedMessageId) {
        lastLoadedMessageSeq = lastLoadedMessageId;
    }

    @Override
    public int getLastLoadedMessageId() {
        return lastLoadedMessageSeq;
    }
}
