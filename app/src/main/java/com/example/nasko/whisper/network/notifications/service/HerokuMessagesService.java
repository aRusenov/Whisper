package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.dto.MessageSentAck;
import com.example.nasko.whisper.models.dto.MessagesQueryResponse;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.network.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;

public class HerokuMessagesService implements MessagesService {

    public static final String EVENT_SHOW_MESSAGES = "show messages";
    public static final String EVENT_NEW_MESSAGE = "new message";

    public static final String EMIT_SHOW_MESSAGES = "show messages";
    public static final String EMIT_SEND_MESSAGE = "send message";

    private static final String TAG = HerokuContactsService.class.getName();

    private SocketManager socketManager;
    private JsonDeserializer deserializer = new JsonDeserializer();

    public HerokuMessagesService(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public Observable<MessagesQueryResponse> onLoadMessages() {
        return socketManager.on(EVENT_SHOW_MESSAGES, MessagesQueryResponse.class);
    }

    @Override
    public Observable<Message> onNewMessage() {
        return socketManager.on(EVENT_NEW_MESSAGE, Message.class);
    }

    @Override
    public Observable<MessageSentAck> onMessageSent() {
        return socketManager.on("send message:ack", MessageSentAck.class);
    }

    // TODO: Extract constants
    // TODO: possibly refactor entire module lol
    public Observable<TypingEvent> onStartTyping() {
        return socketManager.on("start typing", TypingEvent.class);
    }

    public Observable<TypingEvent> onStopTyping() {
        return socketManager.on("stop typing", TypingEvent.class);
    }

    public void startTyping(String chatId, String username) {
        emitTyping(chatId, username, "start typing");
    }

    public void stopTyping(String chatId, String username) {
        emitTyping(chatId, username, "stop typing");
    }

    private void emitTyping(String chatId, String username, String event) {
        JSONObject data = new JSONObject();
        try {
            data.put("chatId", chatId);
            data.put("username", username);
            socketManager.emit(event, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadMessages(String chatId, int offset, int limit) {
        JSONObject data = new JSONObject();
        try {
            data.put("chatId", chatId);
            data.put("from", offset);
            data.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_SHOW_MESSAGES, data);
    }

    @Override
    public void sendMessage(String chatId, String message, long msgIdentifier) {
        JSONObject messageData = new JSONObject();
        try {
            messageData.put("chatId", chatId);
            messageData.put("text", message);
            messageData.put("identifier", msgIdentifier);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_SEND_MESSAGE, messageData);
//        return Observable.create(subscriber -> {
//            socketManager.emit(EMIT_SEND_MESSAGE, new Object[]{messageData}, args -> {
//                if (subscriber.isUnsubscribed()) { // Necessary?
//                    return;
//                }
//
//                try {
//                    MessageSentAck ack = deserializer.deserialize(args[0].toString(), MessageSentAck.class);
//                    subscriber.onNext(ack);
//                    subscriber.onCompleted();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        });
    }
}
