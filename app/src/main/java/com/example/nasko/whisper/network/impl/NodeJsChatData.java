package com.example.nasko.whisper.network.impl;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.network.ChatData;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NodeJsChatData implements ChatData {

    private Socket socket;
    private MessagesEventListener listener;

    public NodeJsChatData(Socket socket) {
        this.socket = socket;

        socket.on("show messages", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String chatId = (String) data.get("chatId");
                    JSONArray msgArr = data.getJSONArray("messages");
                    final List<Message> messages = new ArrayList<>(msgArr.length());
                    for (int i = msgArr.length() - 1; i >= 0; i--) {

                        JSONObject json = (JSONObject) msgArr.get(i);
                        messages.add(new Message(json, chatId));
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onMessagesLoaded(messages);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("new message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject json = (JSONObject) args[0];
                try {
                    final Message message = new Message(json);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onMessageAdded(message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void requestMessages(String username, String chatId, int offset, int limit) {
        JSONObject data = new JSONObject();

        try {
            data.put("username", username);
            data.put("chatId", chatId);
            data.put("from", offset);
            data.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("show messages", data);
    }

    @Override
    public void sendMessage(String username, String chatId, String text) {
        JSONObject messageData = new JSONObject();
        try {
            messageData.put("username", username);
            messageData.put("chatId", chatId);
            messageData.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.socket.emit("send message", messageData);
    }

    @Override
    public void setMessagesEventListener(MessagesEventListener listener) {
        this.listener = listener;
    }
}
