package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.models.Message;

import java.util.List;

public interface ChatroomView {

    void addMessage(Message message);

    void loadMessages(List<Message> messages);
}
