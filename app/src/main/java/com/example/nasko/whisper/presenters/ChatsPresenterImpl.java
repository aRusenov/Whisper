package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.LocalUserRepository;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.Error;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.listeners.AuthenticationListener;
import com.example.nasko.whisper.network.listeners.ContactsEventListener;
import com.example.nasko.whisper.network.listeners.OnSuccessListener;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceConsumer;
import com.example.nasko.whisper.views.contracts.ChatsView;
import com.example.nasko.whisper.views.contracts.ChatsViewNavigator;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.List;

public class ChatsPresenterImpl implements ChatsPresenter, OnSuccessListener<Object> {

    private static final String TAG = ChatsPresenterImpl.class.getName();

    private Context context;
    private ChatsView chatsView;
    private ContactsSearchView contactsSearchView;
    private ChatsViewNavigator chatsViewNavigator;

    private SocketServiceConsumer socketServiceConsumer;
    private UserProvider userProvider;
    private LocalUserRepository localUserRepository;

    private boolean hasPendingContactQuery;
    private String contactQuery;
    private boolean isLogged;

    public ChatsPresenterImpl() {
        this(WhisperApplication.instance().getServiceConsumer(),
                WhisperApplication.instance().getUserProvider());
    }

    public ChatsPresenterImpl(SocketServiceConsumer serviceConsumer, UserProvider userProvider) {
        socketServiceConsumer = serviceConsumer;
        socketServiceConsumer.setOnConnectedListener(this);
        this.userProvider = userProvider;
        setupListeners();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSettingsClicked() {
        User currentUser = userProvider.getCurrentUser();
        if (currentUser != null) {
            chatsViewNavigator.navigateToProfileScreen(currentUser);
        }
    }

    private void setupListeners() {
        socketServiceConsumer.setContactsEventListener(new ContactsEventListener() {
            @Override
            public void onContactsLoaded(List<Chat> chats) {
                Log.d(TAG, "Loading fresh chats");
                if (chatsView != null) {
                    for (int i = 0; i < chats.size(); i++) {
                        setOtherContact(chats.get(i));
                    }

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
                setOtherContact(chat);
                if (chatsView != null) {
                    chatsView.addChat(chat);
                }
                if (contactsSearchView != null) {
                    contactsSearchView.markContactAsFriend(chat.getOtherContact());
                }
            }
        });

        socketServiceConsumer.setContactsQueryEventListener((response) -> {
            if (contactQuery.equals(response.getSearch())) {
                hasPendingContactQuery = false;
            } else {
                socketServiceConsumer.searchContacts(contactQuery);
                Log.d(TAG, "Performing contact query: " + contactQuery);
            }

            contactsSearchView.loadQueryResults(response.getContacts());
        });

        socketServiceConsumer.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticated(User user) {
                Log.d(TAG, "Authentication successful - loading chats");
                socketServiceConsumer.loadContacts();
            }

            @Override
            public void onUnauthorized(Error error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void setOtherContact(Chat chat) {
        User currentUser = userProvider.getCurrentUser();
        List<Contact> participants = chat.getParticipants();
        int i;
        for (i = 0; i < participants.size(); i++) {
            String participantId = participants.get(i).getId();
            if (! participantId.equals(currentUser.getUId())) {
                break;
            }
        }

        chat.setOtherContact(participants.get(i));
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
        chatsViewNavigator.navigateToChatroom(clickedChat, userProvider.getCurrentUser());
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
        if (! contact.isFriend()) {
            socketServiceConsumer.addContact(contact.getId());
        }
    }

    @Override
    public void onLogout() {
        LocalUserRepository localUserRepository = new LocalUserRepository(context);
        localUserRepository.logout();
        socketServiceConsumer.stop(true);
        chatsViewNavigator.navigateToLoginScreen();
    }

    @Override
    public void onCreate() {
        User currentUser = userProvider.getCurrentUser();
        if (currentUser == null) {
            localUserRepository = new LocalUserRepository(context);
            User loggedUser = localUserRepository.getLoggedUser();
            if (loggedUser.getSessionToken() == null) {
                onLogout();
            } else {
                Log.d(TAG, loggedUser.getSessionToken());
                userProvider.setCurrentUser(loggedUser);
            }
        }
    }

    @Override
    public void onDestroy() {
//        socketServiceConsumer.stop(false);
    }

    @Override
    public void onResume() {
        User currentUser = userProvider.getCurrentUser();
        socketServiceConsumer.resume();
        if (currentUser == null) {
            return;
        }

        if (socketServiceConsumer.isBound()) {
            socketServiceConsumer.loadContacts();
        } else {
            socketServiceConsumer.start(currentUser.getSessionToken());
        }
    }

    @Override
    public void onPause() {
        socketServiceConsumer.pause();
    }

    @Override
    public void onSuccess(Object o) {
        socketServiceConsumer.loadContacts();
    }
}
