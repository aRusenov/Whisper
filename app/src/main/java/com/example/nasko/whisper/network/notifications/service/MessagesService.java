package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessagesQueryResponse;
import com.example.nasko.whisper.models.TypingEvent;

import rx.Observable;

public interface MessagesService {

    Observable<MessagesQueryResponse> onLoadMessages();

    Observable<Message> onNewMessage();

    void loadMessages(String chatId, int offset, int limit);

    void sendMessage(String chatId, String message);

    Observable<TypingEvent> onStartTyping();

    Observable<TypingEvent> onStopTyping();

    void startTyping(String chatId, String username);

    void stopTyping(String chatId, String username);
}
