package com.example.nasko.whisper.network.notifications.service;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessagesQueryResponse;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;
import com.example.nasko.whisper.network.misc.JsonDeserializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.client.Socket;

public class HerokuMessagesService implements MessagesService {

    private static final String TAG = "HerokuMessagesService";

    private Socket socket;
    private MessagesEventListener messagesEventListener;
    private OnNewMessageListener newMessageListener;
    private JsonDeserializer deserializer;

    public HerokuMessagesService(Socket socket, JsonDeserializer deserializer) {
        this.deserializer = deserializer;
        this.socket = socket;
        this.registerEventListeners();
    }

    private void registerEventListeners() {
        socket.on("show messages", args -> {
            String json = args[0].toString();
            try {
                MessagesQueryResponse messages = deserializer.deserialize(json, MessagesQueryResponse.class);
                if (messagesEventListener != null) {
                    messagesEventListener.onMessagesLoaded(messages);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).on("new message", args -> {
            String json = args[0].toString();
            Message newMessage = null;
            try {
                newMessage = deserializer.deserialize(json, Message.class);
                if (messagesEventListener != null) {
                    messagesEventListener.onMessageAdded(newMessage);
                }

                if (newMessageListener != null) {
                    newMessageListener.onNewMessage(newMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setMessagesEventListener(MessagesEventListener listener) {
        this.messagesEventListener = listener;
    }

    @Override
    public void setNewMessageEventListener(OnNewMessageListener listener) {
        this.newMessageListener = listener;
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

        this.socket.emit("show messages", data);
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

        this.socket.emit("send message", messageData);
    }

    @Override
    public void clearListeners() {
        messagesEventListener = null;
    }
}
