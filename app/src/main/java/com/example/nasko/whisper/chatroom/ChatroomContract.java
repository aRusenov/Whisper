package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.BaseView;

import java.util.List;

public interface ChatroomContract {

    interface View extends BaseView {

        void addMessage(MessageViewModel message);

        void updateMessageStatus(long identifier, MessageStatus status);

        void loadMessages(List<MessageViewModel> messages);

        void displayTypingStarted(TypingEvent typingEvent);

        void displayTypingStopped();

        void displayNetworkError();

        void hideNetworkError();
    }

    interface Presenter extends BasePresenter {

        MessageViewModel onMessageSend(String text, ContactViewModel userContact);

        void setLastLoadedMessageId(int lastLoadedMessageId);

        int getLastLoadedMessageId();

        boolean onScrollToTop();

        void onStartTyping();

        void onStopTyping();
    }
}
