package com.example.nasko.whisper.presenters.chats;

import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.SocketServicePresenter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ContactsSearchPresenterImpl extends SocketServicePresenter<ContactsSearchView> implements ContactsSearchPresenter {

    private static final String TAG = ContactsSearchPresenterImpl.class.getName();

    private UserProvider userProvider;
    private Subscription contactsQuerySub;
    private Subscription newChatSub;

    private boolean hasPendingQuery;
    private String contactQuery;

    public ContactsSearchPresenterImpl() {
        this(WhisperApplication.instance().getServiceConsumer(),
                WhisperApplication.instance().getUserProvider());
    }

    public ContactsSearchPresenterImpl(SocketServiceBinder serviceBinder,
                                       UserProvider userProvider) {
        super(serviceBinder);
        this.userProvider = userProvider;
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        contactsQuerySub = service.contactsService()
                .onContactQueryResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    User currentUser = userProvider.getCurrentUser();
                    for (Contact contact : response.getContacts()) {
                        if (contact.getId().equals(currentUser.getUId())) {
                            contact.setUser(true);
                        }
                    }

                    view.loadQueryResults(response.getContacts());

                    if (contactQuery.equals(response.getSearch())) {
                        hasPendingQuery = false;
                    } else {
                        service.contactsService().searchContacts(contactQuery);
                        Log.d(TAG, "Performing contact query: " + contactQuery);
                    }
                });

        newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    setOtherContact(chat);
                    view.markContactAsFriend(chat.getOtherContact());
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
    public void detachView() {
        super.detachView();

        contactsQuerySub.unsubscribe();
        newChatSub.unsubscribe();
    }

    @Override
    public void onQueryEntered(String query) {
        if (query.length() < 2) {
            return;
        }

        contactQuery = query;
        if (!hasPendingQuery) {
            hasPendingQuery = true;
            service.contactsService().searchContacts(query);
            Log.d(TAG, "Performing contact query: " + query);
        }
    }

    @Override
    public void onContactSendRequestClick(Contact contact) {
        Log.d(TAG, "Performing add contact query");
        if (!contact.isFriend() && !contact.isUser()) {
            service.contactsService().addContact(contact.getId());
        }
    }
}
