package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.helpers.Mapper;
import com.example.nasko.whisper.data.socket.consumer.MessageNotificationController;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatroomPresenter extends ServiceBoundPresenter implements ChatroomContract.Presenter {

    private static final int PAGE_SIZE = 10;
    private static final int DEFAULT_MESSAGE_SEQ = -1;

    private ChatViewModel chat;
    private int lastLoadedMessageSeq = DEFAULT_MESSAGE_SEQ;
    private boolean loadingMessages;

    private ChatroomContract.View view;
    private MessageNotificationController notificationController;

    public ChatroomPresenter(ChatroomContract.View view, ChatViewModel chat, SocketServiceBinder serviceBinder,
                             MessageNotificationController notificationController, UserProvider userProvider) {
        super(serviceBinder, userProvider);
        this.view = view;
        this.chat = chat;
        this.notificationController = notificationController;
    }

    @Override
    public void start() {
        super.start();
        notificationController.removeNotification(chat.getDisplayContact().getId()); // TODO: Use chatId instead
    }

    @Override
    public void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions) {
        if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
            loadMesages();
        }

        Subscription authSub = service.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideNetworkError();
                    if (lastLoadedMessageSeq == DEFAULT_MESSAGE_SEQ) {
                        loadMesages();
                    }
                });

        serviceSubscriptions.add(authSub);

        Subscription disconnectSub = service.connectionService()
                .onDisconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    view.displayNetworkError();
                });

        serviceSubscriptions.add(disconnectSub);

        Subscription loadMessagesSub = service.messageService()
                .onLoadMessages()
                .filter(response -> response.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadingMessages = false;

                    List<Message> messages = response.getMessages();
                    if (messages.size() > 0) {
                        lastLoadedMessageSeq = messages.get(0).getSeq();
                        List<MessageViewModel> viewModelList = Mapper.toMessageViewModelList(messages);
                        view.loadMessages(viewModelList);
                    }
                });

        serviceSubscriptions.add(loadMessagesSub);

        Subscription newMsgSub = service.messageService()
                .onNewMessage()
                .filter(message -> message.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    MessageViewModel msg = Mapper.toMessageViewModel(message);
                    view.addMessage(msg);
                });

        serviceSubscriptions.add(newMsgSub);

        Subscription messageSentSub = service.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    view.updateMessageStatus(messageSentAck.getMessageIdentifier(), MessageStatus.SENT);
                });

        serviceSubscriptions.add(messageSentSub);

        Subscription startTypingSub = service.messageService()
                .onStartTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStarted(typingEvent);
                });

        serviceSubscriptions.add(startTypingSub);

        Subscription stopTypingSub = service.messageService()
                .onStopTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStopped(typingEvent);
                });

        serviceSubscriptions.add(stopTypingSub);
    }

    @Override
    public void onServiceUnbind() { }

    @Override
    public void onMessageSend(String text, long msgIdentifier) {
        if (service != null) {
            service.messageService().sendMessage(chat.getId(), text.trim(), msgIdentifier);
        }
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
    public void setLastLoadedMessageId(int lastLoadedMessageId) {
        lastLoadedMessageSeq = lastLoadedMessageId;
    }

    @Override
    public int getLastLoadedMessageId() {
        return lastLoadedMessageSeq;
    }

    private void loadMesages() {
        if (service != null) {
            service.messageService().loadMessages(chat.getId(), lastLoadedMessageSeq, PAGE_SIZE * 2);
            loadingMessages = true;
        }
    }
}
