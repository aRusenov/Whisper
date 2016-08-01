package com.example.nasko.whisper.network.listeners;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessagesQueryResponse;

public interface MessagesEventListener {

    void onMessageAdded(Message message);

    void onMessagesLoaded(MessagesQueryResponse messages);
}
