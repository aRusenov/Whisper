package com.example.nasko.whisper.network.notifications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;

import java.util.List;

public class SocketServiceReceiver {

    public void searchContacts(String contactQuery) {
        android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_SEARCH_CONTACTS, contactQuery);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void loadContacts() {
        android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_SHOW_CHATS);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addContact(String id) {
        android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_ADD_CONTACT, id);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text, String chatId) {
        Message message = new Message(text, chatId);
        android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_SEND_MESSAGE, message);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void loadMessages(String chatId, int lastLoadedMessageId, int limit) {
        Bundle bundle = new Bundle();
        bundle.putString("chatId", chatId);
        bundle.putInt("from", lastLoadedMessageId);
        bundle.putInt("limit", limit);

        android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_LOAD_MESSAGES);
        msg.setData(bundle);
        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class IncomingHandler extends Handler {

        Context context;

        IncomingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageTypes.MSG_AUTHENTICATED:
                    if (authenticationListener != null) {
                        User user = (User) msg.obj;
                        authenticationListener.onAuthenticated(user);
                    }
                    break;
                case MessageTypes.MSG_NEW_MESSAGE:
                    if (messagesEventListener != null) {
                        Message message = (Message) msg.obj;
                        messagesEventListener.onMessageAdded(message);
                    }
                    break;
                case MessageTypes.MSG_MESSAGES_LOADED:
                    if (messagesEventListener != null) {
                        List<Message> messages = (List<Message>) msg.obj;
                        messagesEventListener.onMessagesLoaded(messages);
                    }
                    break;
                case MessageTypes.MSG_CHAT_ADDED: {
                    Chat chat = (Chat) msg.obj;
                    contactsEventListener.onContactAdded(chat);
                    break;
                }
                case MessageTypes.MSG_CHAT_UPDATED: {
                    Chat chat = (Chat) msg.obj;
                    contactsEventListener.onContactUpdated(chat);
                    break;
                }
                case MessageTypes.MSG_CHATS_LOADED:
                    List<Chat> chats = (List<Chat>) msg.obj;
                    contactsEventListener.onContactsLoaded(chats);
                    break;
                case MessageTypes.MSG_CONTACTS_QUERY_RESULT:
                    // TODO:
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private static final String TAG = "SocketServiceReceiver";

    private Context context;
    private Messenger serviceMessenger;
    private Messenger clientMessenger;
    private boolean isBound;

    private MessagesEventListener messagesEventListener;
    private ContactsQueryEventListener contactsQueryEventListener;
    private ContactsEventListener contactsEventListener;
    private AuthenticationListener authenticationListener;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "Connected to service");
            serviceMessenger = new Messenger(binder);
            android.os.Message msg = android.os.Message.obtain(null, MessageTypes.MSG_REGISTER_CLIENT);
            msg.replyTo = clientMessenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "Remote exception in " + TAG);
            }

            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from service");
            serviceMessenger = null;
            isBound = false;
        }
    };

    public SocketServiceReceiver(Context context) {
        this.context = context;
        this.clientMessenger = new Messenger(new IncomingHandler(context));
    }

    public void setContactsEventListener(ContactsEventListener contactsEventListener) {
        this.contactsEventListener = contactsEventListener;
    }

    public void setContactsQueryEventListener(ContactsQueryEventListener contactsQueryEventListener) {
        this.contactsQueryEventListener = contactsQueryEventListener;
    }

    public void setMessagesEventListener(MessagesEventListener messagesEventListener) {
        this.messagesEventListener = messagesEventListener;
    }

    public void setAuthenticationListener(AuthenticationListener authenticationListener) {
        this.authenticationListener = authenticationListener;
    }

    public void start(String token) {
        if (!isBound) {
            Intent intent = new Intent("START_SERVICE");
            intent.putExtra("token", token);
            intent.setPackage(context.getPackageName());
            context.bindService(intent, connection, context.BIND_AUTO_CREATE);
            context.startService(intent);
        }
    }

    public void stop(boolean closeService) {
        if (closeService) {
            Intent intent = new Intent("START_SERVICE");
            intent.setPackage(context.getPackageName());
            context.stopService(intent);
        } else if (isBound) {
            android.os.Message unregisterMsg = android.os.Message.obtain(null, MessageTypes.MSG_UNREGISTER_CLIENT);
            try {
                serviceMessenger.send(unregisterMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            context.unbindService(connection);
        }
    }
}
