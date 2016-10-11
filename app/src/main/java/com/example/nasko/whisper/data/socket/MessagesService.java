package com.example.nasko.whisper.data.socket;

import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.dto.MessagesQueryResponse;
import com.example.nasko.whisper.models.TypingEvent;

import rx.Observable;

public interface MessagesService {

    Observable<MessagesQueryResponse> onLoadMessages();

    Observable<Message> onNewMessage();

    Observable<MessageSentAck> onMessageSent();

    void loadMessages(String chatId, int offset, int limit);

    void sendMessage(String chatId, String message, long msgIdentifier);

    Observable<TypingEvent> onStartTyping();

    Observable<TypingEvent> onStopTyping();

    void startTyping(String chatId, String username);

    void stopTyping(String chatId, String username);
}
