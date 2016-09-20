package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.view.MessageViewModel;

import java.util.List;

public interface ChatroomView extends View {

    void addMessage(MessageViewModel message);

    void updateMessageStatus(long identifier, MessageStatus status);

    void loadMessages(List<MessageViewModel> messages);

    void displayTypingStarted(TypingEvent typingEvent);

    void displayTypingStopped(TypingEvent typingEvent);
}
