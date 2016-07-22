package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.OnAuthenticatedListener;
import com.example.nasko.whisper.network.listeners.SocketStateListener;
import com.example.nasko.whisper.network.notifications.ContactsService;
import com.example.nasko.whisper.network.notifications.SocketService;
import com.example.nasko.whisper.views.contracts.ChatsActionBarView;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.List;

public class ChatsPresenterImpl implements ChatsPresenter {

    private static final String TAG = "ChatsPresenter";

    private Context context;
    private ChatsView chatsView;
    private ChatsActionBarView chatsActionBarView;
    private ContactsService contactsService;
    private SocketService socketService;

    private boolean hasPendingContactQuery;
    private String contactQuery;

    public ChatsPresenterImpl(Context context) {
        this.context = context;
        socketService = WhisperApplication.getInstance().getSocketService();
        contactsService = socketService.getContactsService();
        setupListeners();
    }

    private void setupListeners() {
        contactsService.setContactsEventListener(new ContactsEventListener() {
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
            }
        });

        contactsService.setContactsQueryEventListener((contacts, query) -> {
            if (contactQuery.equals(query)) {
                hasPendingContactQuery = false;
            } else {
                contactsService.searchContacts(contactQuery);
                Log.d(TAG, "Performing contact query: " + contactQuery);
            }

            chatsActionBarView.loadQueryResults(contacts);
        });

        socketService.setAuthenticatedListener(new OnAuthenticatedListener() {
            @Override
            public void onAuthenticated(User user) {
                Log.d(TAG, "Authentication successful - loading chats");
                contactsService.loadContacts();
            }

            @Override
            public void onUnauthorized(Error error) {
                Log.e(TAG, error.getMessage());
            }
        });

        socketService.setSocketStateListener(new SocketStateListener() {
            @Override
            public void onConnect() {
                Log.d(TAG, "Connected");
            }

            @Override
            public void onConnectionTimeout() {

            }

            @Override
            public void onConnectionError() {
                Log.d(TAG, "Error connecting");
            }

            @Override
            public void onDisconnect() {
                Log.d(TAG, "Disconnected");
            }
        });
    }

    @Override
    public void onTakeViews(ChatsView chatsView, ChatsActionBarView actionBarView) {
        this.chatsView = chatsView;
        this.chatsActionBarView = actionBarView;
    }

    @Override
    public void onChatClicked(Chat clickedChat) {
        chatsView.navigateToChatroom(clickedChat);
    }

    @Override
    public void onQueryEntered(String query) {
        if (query.length() < 2) {
            return;
        }

        contactQuery = query;
        if (!hasPendingContactQuery) {
            hasPendingContactQuery = true;
            contactsService.searchContacts(query);
            Log.d(TAG, "Performing contact query: " + query);
        }
    }

    @Override
    public void onContactSendRequestClick(Contact contact) {

    }

    @Override
    public void onLogout() {
        LocalUserRepository localUserRepository = new LocalUserRepository(this.context);
        localUserRepository.logout();
        socketService.clearContactsService();
        socketService.clearMessagesService();
        socketService.setCurrentUser(null);
        chatsView.navigateToLoginScreen();
    }

    @Override
    public void onResume() {
        if (!socketService.connected()) {
            Log.d(TAG, "Loading and authenticating...");
            socketService.connect();
            socketService.authenticate(socketService.getCurrentUser().getSessionToken());
        } else {
            Log.d(TAG, "Directly loading contacts");
            contactsService.loadContacts();
        }
    }

    @Override
    public User getCurrentUser() {
        return socketService.getCurrentUser();
    }
}
