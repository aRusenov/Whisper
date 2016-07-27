package com.example.nasko.whisper.network.notifications;

import android.os.Messenger;
import android.util.Log;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class HerokuMessagesService implements MessagesService {

    private static final String TAG = "HerokuMessagesService";

    private User currentUser;
    private Socket socket;
    private MessagesEventListener messagesEventListener;
    private Messenger messenger;
    private MessageBroadcaster messageBroadcaster;

    public HerokuMessagesService(Socket socket, MessageBroadcaster messageBroadcaster) {
        this.messageBroadcaster = messageBroadcaster;
        this.socket = socket;
        this.registerEventListeners();
    }

    private void registerEventListeners() {
        Log.d("MessagesService", "Has listeners? " + socket.hasListeners("show messages"));
        Log.d("MessagesService", "Attaching event listeners");

        socket.on("show messages", args -> {
            Log.d(TAG, "Showing messages");
            JSONObject data = (JSONObject) args[0];
            try {
                String chatId = (String) data.get("chatId");
                JSONArray msgArr = data.getJSONArray("messages");
                final List<Message> messages = new ArrayList<>(msgArr.length());
                for (int i = msgArr.length() - 1; i >= 0; i--) {

                    JSONObject json = (JSONObject) msgArr.get(i);
                    messages.add(new Message(json, chatId));
                }

                android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_MESSAGES_LOADED, messages);
                messageBroadcaster.sendMessage(msg);
//                if (messagesEventListener != null) {
//                    new Handler(Looper.getMainLooper()).post(() ->
//                            messagesEventListener.onMessagesLoaded(messages));
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).on("new message", args -> {
            Log.d(TAG, "New message");
            JSONObject json = (JSONObject) args[0];
            try {
                final Message message = new Message(json);
                android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_NEW_MESSAGE, message);
                Log.d(TAG, "Sending new message to client");
                messageBroadcaster.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
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
        this.messagesEventListener = null;
//        socket.off("show messages")
//                .off("new message");
    }
}
