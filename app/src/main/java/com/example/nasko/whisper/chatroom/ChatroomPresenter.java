package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.notifications.MessageNotificationController;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.Date;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChatroomPresenter extends SocketPresenter implements ChatroomContract.Presenter {

    private static final int PAGE_SIZE = 10;


    private ChatViewModel chat;
    private int lastLoadedMessageSeq;
    private boolean loadingMessages;

    private ChatroomContract.View view;
    private MessageNotificationController notificationController;

    public ChatroomPresenter(ChatroomContract.View view, ChatViewModel chat, int lastLoadedMessageSeq,
                             SocketService socketService, MessageNotificationController notificationController,
                             UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
        this.chat = chat;
        this.lastLoadedMessageSeq = lastLoadedMessageSeq;
        this.notificationController = notificationController;
    }

    @Override
    public void init() {
        notificationController.removeNotification(chat.getId());
        if (lastLoadedMessageSeq == -1 && socketService.authenticated()) {
            loadMesages();
        }

        Subscription authSub = socketService.connectionService()
                .onAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideNetworkError();
                    if (lastLoadedMessageSeq == -1) {
                        loadMesages();
                    }
                });

        subscriptions.add(authSub);

        Subscription disconnectSub = socketService.connectionService()
                .onDisconnect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe($ -> {
                    view.displayNetworkError();
                });

        subscriptions.add(disconnectSub);

        Subscription loadMessagesSub = socketService.messageService()
                .onLoadMessages()
                .filter(response -> response.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadingMessages = false;

                    List<Message> messages = response.getMessages();
                    lastLoadedMessageSeq = messages.get(0).getSeq();
                    List<MessageViewModel> viewModelList = Mapper.toMessageViewModelList(messages);
                    view.loadMessages(viewModelList);
                });

        subscriptions.add(loadMessagesSub);

        Subscription newMsgSub = socketService.messageService()
                .onNewMessage()
                .filter(message -> message.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    MessageViewModel msg = Mapper.toMessageViewModel(message);
                    view.addMessage(msg);
                });

        subscriptions.add(newMsgSub);

        Subscription messageSentSub = socketService.messageService()
                .onMessageSent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageSentAck -> {
                    view.updateMessageStatus(messageSentAck.getMessageIdentifier(), MessageStatus.SENT);
                });

        subscriptions.add(messageSentSub);

        Subscription startTypingSub = socketService.messageService()
                .onStartTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStarted(typingEvent);
                });

        subscriptions.add(startTypingSub);

        Subscription stopTypingSub = socketService.messageService()
                .onStopTyping()
                .filter(typingEvent -> typingEvent.getChatId().equals(chat.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(typingEvent -> {
                    view.displayTypingStopped();
                });

        subscriptions.add(stopTypingSub);
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }

    @Override
    public MessageViewModel onMessageSend(String text, ContactViewModel userContact) {
        MessageViewModel newMessage = new MessageViewModel(chat.getId(), text, new Date(), userContact);
        newMessage.setStatus(MessageStatus.PENDING);
        newMessage.setUId(System.nanoTime());

        socketService.messageService().sendMessage(chat.getId(), text.trim(), newMessage.getIdentifier());
        return newMessage;
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
        socketService.messageService().startTyping(chat.getId(), userProvider.getCurrentUser().getUsername());
    }

    @Override
    public void onStopTyping() {
        socketService.messageService().stopTyping(chat.getId(), userProvider.getCurrentUser().getUsername());
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
        socketService.messageService().loadMessages(chat.getId(), lastLoadedMessageSeq, PAGE_SIZE * 2);
        loadingMessages = true;
    }
}
