package com.example.nasko.whisper.data.nodejs;

import android.os.Handler;
import android.os.Looper;

import com.example.nasko.whisper.Message;
import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.listeners.MessagesEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    final Message[] messages = new Message[msgArr.length()];
                    for (int i = 0; i < msgArr.length(); i++) {

                        JSONObject json = (JSONObject) msgArr.get(i);
                        messages[i] = new Message(json, chatId);
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
