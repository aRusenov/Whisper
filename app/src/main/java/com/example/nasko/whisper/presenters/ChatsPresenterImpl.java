package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;
import com.example.nasko.whisper.network.notifications.SocketServiceConsumer;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.example.nasko.whisper.views.contracts.ChatsViewNavigator;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.List;

public class ChatsPresenterImpl implements ChatsPresenter, OnSuccessListener<Object> {

    private static final String TAG = "ChatsPresenter";

    private Context context;
    private ChatsView chatsView;
    private ContactsSearchView contactsSearchView;
    private ChatsViewNavigator chatsViewNavigator;

    private SocketServiceConsumer socketServiceConsumer;
    private LocalUserRepository localUserRepository;

    private boolean hasPendingContactQuery;
    private String contactQuery;
    private boolean isLogged;

    public ChatsPresenterImpl() {
        socketServiceConsumer = WhisperApplication.instance().getServiceReceiver();
        socketServiceConsumer.setOnConnectedListener(this);
        setupListeners();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void setupListeners() {
        socketServiceConsumer.setContactsEventListener(new ContactsEventListener() {
            @Override
            public void onContactsLoaded(List<Chat> chats) {
                Log.d(TAG, "Loading fresh chats");
                if (chatsView != null) {
                    chatsView.clearChats();
                    chatsView.loadChats(chats);
                }
            }

            @Override
            public void onContactUpdated(Chat chat) {
                if (chatsView != null) {
                    chatsView.updateChat(chat);
                }
            }

            @Override
            public void onContactAdded(Chat chat) {
                if (chatsView != null) {
                    chatsView.addChat(chat);
                }
                if (contactsSearchView != null) {
                    contactsSearchView.markContactAsFriend(chat.getOtherContact());
                }
            }
        });

        socketServiceConsumer.setContactsQueryEventListener((contacts, query) -> {
            if (contactQuery.equals(query)) {
                hasPendingContactQuery = false;
            } else {
                socketServiceConsumer.searchContacts(contactQuery);
                Log.d(TAG, "Performing contact query: " + contactQuery);
            }

            contactsSearchView.loadQueryResults(contacts);
        });

        socketServiceConsumer.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticated(User user) {
                Log.d(TAG, "Authentication successful - loading chats");
//                socketService.setCurrentUser(user);
                socketServiceConsumer.loadContacts();
            }

            @Override
            public void onUnauthorized(Error error) {
                Log.e(TAG, error.getMessage());
            }
        });

//        socketServiceConsumer.setSocketStateListener(new SocketStateListener() {
//            @Override
//            public void onConnect() {
//                Log.d(TAG, "Connected");
//                chatsViewNavigator.setNetworkStatus("Whisper");
//            }
//
//            @Override
//            public void onConnectionTimeout() {
//                Log.d(TAG, "Timeout");
//                chatsViewNavigator.setNetworkStatus("Connecting...");
//            }
//
//            @Override
//            public void onConnectionError() {
//                Log.d(TAG, "Error connecting");
//                chatsViewNavigator.setNetworkStatus("Connecting...");
//            }
//
//            @Override
//            public void onDisconnect() {
//                Log.d(TAG, "Disconnected");
//                chatsViewNavigator.setNetworkStatus("Connecting...");
//            }
//        });
    }

    @Override
    public void onTakeChatsViewNavigator(ChatsViewNavigator chatsViewNavigator) {
        this.chatsViewNavigator = chatsViewNavigator;
    }

    @Override
    public void onTakeChatsView(ChatsView chatsView) {
        this.chatsView = chatsView;
    }

    @Override
    public void onTakeContactsSearchView(ContactsSearchView actionBarView) {
        this.contactsSearchView = actionBarView;
    }

    @Override
    public void onChatClicked(Chat clickedChat) {
        chatsViewNavigator.navigateToChatroom(clickedChat);
    }

    @Override
    public void onQueryEntered(String query) {
        if (query.length() < 2) {
            return;
        }

        contactQuery = query;
        if (!hasPendingContactQuery) {
            hasPendingContactQuery = true;
            socketServiceConsumer.searchContacts(query);
            Log.d(TAG, "Performing contact query: " + query);
        }
    }

    @Override
    public void onContactSendRequestClick(Contact contact) {
        Log.d(TAG, "Performing add contact query");
        socketServiceConsumer.addContact(contact.getId());
    }

    @Override
    public void onLogout() {
        LocalUserRepository localUserRepository = new LocalUserRepository(this.context);
        localUserRepository.logout();
        socketServiceConsumer.stop(true);
        chatsViewNavigator.navigateToLoginScreen();
    }

    @Override
    public void onCreate() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            localUserRepository = new LocalUserRepository(context);
            User loggedUser = localUserRepository.getLoggedUser();
            if (loggedUser.getSessionToken() == null) {
                onLogout();
            } else {
                setCurrentUser(loggedUser);
                socketServiceConsumer.start(loggedUser.getSessionToken());
            }
        } else {
            if (socketServiceConsumer.isBound()) {
                socketServiceConsumer.loadContacts();
            } else {
                socketServiceConsumer.start(currentUser.getSessionToken());
            }
        }
    }

    @Override
    public void onDestroy() {
//        socketServiceConsumer.stop(false);
    }

    @Override
    public void onResume() {
        if (socketServiceConsumer.isBound()) {
            socketServiceConsumer.loadContacts();
        }
    }

    @Override
    public User getCurrentUser() {
        return WhisperApplication.instance().getCurrentUser();
    }

    public void setCurrentUser(User user) {
        WhisperApplication.instance().setCurrentUser(user);
    }

    @Override
    public void onSuccess(Object o) {
        socketServiceConsumer.loadContacts();
    }
}
