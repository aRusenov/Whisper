package com.example.nasko.whisper.network.notifications;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.ContactsQueryEventListener;
import com.example.nasko.whisper.network.listeners.MessagesEventListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;

import java.util.List;

public class SocketServiceConsumer {

    private static final String TAG = SocketServiceConsumer.class.getName();

    private Context context;
    private boolean isBound;
    private BackgroundSocketService service;

    private OnSuccessListener onConnectedListener;
    private MessagesEventListener messagesEventListener;
    private ContactsQueryEventListener contactsQueryEventListener;
    private ContactsEventListener contactsEventListener;
    private AuthenticationListener authenticationListener;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "Connected to service");
            service = ((BackgroundSocketService.LocalBinder) binder).getService();
            isBound = true;

            if (onConnectedListener != null) {
                onConnectedListener.onSuccess(null);
            }

            service.getContactsService().setContactsEventListener(contactsEventListener);
            service.getContactsService().setContactsQueryEventListener(contactsQueryEventListener);
            service.getSocketService().setAuthenticatedListener(authenticationListener);
            service.getMessagesService().setMessagesEventListener(messagesEventListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Disconnected from service");
            service = null;
            isBound = false;
        }
    };

    public SocketServiceConsumer(Context context) {
        this.context = context;
    }

    public void setOnConnectedListener(OnSuccessListener onConnectedListener) {
        this.onConnectedListener = onConnectedListener;
    }

    public void setContactsEventListener(ContactsEventListener contactsEventListener) {
        this.contactsEventListener = new ContactsEventListener() {
            @Override
            public void onContactsLoaded(List<Chat> chats) {
                runOnMain(() -> contactsEventListener.onContactsLoaded(chats));
            }

            @Override
            public void onContactUpdated(Chat chat) {
                runOnMain(() -> contactsEventListener.onContactUpdated(chat));
            }

            @Override
            public void onContactAdded(Chat chat) {
                runOnMain(() -> contactsEventListener.onContactAdded(chat));
            }
        };

        if (isBound) {
            service.getContactsService().setContactsEventListener(this.contactsEventListener);
        }
    }

    private void runOnMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public void setContactsQueryEventListener(ContactsQueryEventListener contactsQueryEventListener) {
        this.contactsQueryEventListener = new ContactsQueryEventListener() {
            @Override
            public void onContactsLoaded(List<Contact> chats, String query) {
                runOnMain(() -> contactsQueryEventListener.onContactsLoaded(chats, query));
            }
        };

        if (isBound) {
            service.getContactsService().setContactsQueryEventListener(this.contactsQueryEventListener);
        }
    }

    public void setMessagesEventListener(MessagesEventListener messagesEventListener) {
        this.messagesEventListener = new MessagesEventListener() {
            @Override
            public void onMessageAdded(Message message) {
                runOnMain(() -> messagesEventListener.onMessageAdded(message));
            }

            @Override
            public void onMessagesLoaded(List<Message> messages) {
                runOnMain(() -> messagesEventListener.onMessagesLoaded(messages));
            }
        };

        if (isBound) {
            service.getMessagesService().setMessagesEventListener(this.messagesEventListener);
        }
    }

    public void setAuthenticationListener(AuthenticationListener authenticationListener) {
        this.authenticationListener = new AuthenticationListener() {
            @Override
            public void onAuthenticated(User user) {
                runOnMain(() -> authenticationListener.onAuthenticated(user));
            }

            @Override
            public void onUnauthorized(Error error) {
                runOnMain(() -> authenticationListener.onUnauthorized(error));
            }
        };

        if (isBound) {
            service.getSocketService().setAuthenticatedListener(this.authenticationListener);
        }
    }

    public boolean isBound() {
        return isBound;
    }

    public void start(String token) {
        if (!isBound()) {
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
            context.unbindService(connection);
            context.stopService(intent);
        } else if (isBound) {
            service.detachListeners();
            context.unbindService(connection);
        }

        isBound = false;
    }

    public void searchContacts(String contactQuery) {
        validateState();
        service.getContactsService().searchContacts(contactQuery);
    }

    public void loadContacts() {
        validateState();
        service.getContactsService().loadContacts();
    }

    public void addContact(String userId) {
        validateState();
        service.getContactsService().addContact(userId);
    }

    public void sendMessage(String text, String chatId) {
        validateState();
        service.getMessagesService().sendMessage(chatId, text);
    }

    public void loadMessages(String chatId, int lastLoadedMessageId, int limit) {
        validateState();
        service.getMessagesService().loadMessages(chatId, lastLoadedMessageId, limit);
    }

    private void validateState() {
        if (!isBound) {
            throw new UnsupportedOperationException(TAG + " is not bound to service");
        }
    }
}
