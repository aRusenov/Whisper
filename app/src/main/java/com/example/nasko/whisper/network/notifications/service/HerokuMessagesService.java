package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessagesQueryResponse;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class HerokuMessagesService implements MessagesService {

    public static final String EMIT_SHOW_MESSAGES = "show messages";
    public static final String EMIT_SEND_MESSAGE = "send message";
    public static final String EVENT_SHOW_MESSAGES = "show messages";
    public static final String EVENT_NEW_MESSAGE = "new message";

    private static final String TAG = HerokuContactsService.class.getName();

    private SocketManager socketManager;
    private MessagesEventListener messagesEventListener;

    public HerokuMessagesService(SocketManager socketManager) {
        this.socketManager = socketManager;
        this.register();
    }

    private void register() {
        socketManager.on(EVENT_SHOW_MESSAGES, MessagesQueryResponse.class, messagesResponse -> {
            if (messagesEventListener != null) {
                messagesEventListener.onMessagesLoaded(messagesResponse);
            }
        }).on(EVENT_NEW_MESSAGE, Message.class, newMessage -> {
            if (messagesEventListener != null) {
                messagesEventListener.onMessageAdded(newMessage);
            }
        });
    }

    @Override
    public void setMessagesEventListener(MessagesEventListener listener) {
        this.messagesEventListener = listener;
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
    public void sendMessage(String chatId, String message) {
        JSONObject messageData = new JSONObject();
        try {
            messageData.put("chatId", chatId);
            messageData.put("text", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketManager.emit(EMIT_SEND_MESSAGE, messageData);
    }

    @Override
    public void clearListeners() {
        messagesEventListener = null;
    }
}
