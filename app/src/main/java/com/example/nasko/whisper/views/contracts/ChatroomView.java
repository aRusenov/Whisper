package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.TypingEvent;

import java.util.List;

public interface ChatroomView extends View {

    void addMessage(Message message);

    void loadMessages(List<Message> messages);

    void displayTypingStarted(TypingEvent typingEvent);

    void displayTypingStopped(TypingEvent typingEvent);
}
