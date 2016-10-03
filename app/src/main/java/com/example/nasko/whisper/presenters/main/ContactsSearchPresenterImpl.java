package com.example.nasko.whisper.presenters.main;

import android.util.Log;

import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.network.notifications.consumer.SocketServiceBinder;
import com.example.nasko.whisper.network.notifications.service.SocketService;
import com.example.nasko.whisper.presenters.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.Mapper;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class ContactsSearchPresenterImpl extends ServiceBoundPresenter<ContactsSearchView> implements ContactsSearchPresenter {

    private static final String TAG = ContactsSearchPresenterImpl.class.getName();
    private static final long TYPING_EVENT_WAIT_MS = 500;

    private UserProvider userProvider;
    private PublishSubject<String> searchRequestSubject = PublishSubject.create();

    public ContactsSearchPresenterImpl() {
        this(WhisperApplication.instance().getServiceBinder(),
                WhisperApplication.instance().getUserProvider());
    }

    public ContactsSearchPresenterImpl(SocketServiceBinder serviceBinder, UserProvider userProvider) {
        super(serviceBinder);
        this.userProvider = userProvider;
    }

    @Override
    public void onServiceBind(SocketService service) {
        super.onServiceBind(service);
        Subscription contactsQuerySub = service.contactsService()
                .onContactQueryResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    List<ContactViewModel> result = Mapper.toContactViewModelList(response.getContacts());

                    view.showQueryResults(result);
                    view.hideLoading();
                });

        Subscription newChatSub = service.contactsService()
                .onNewChat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chat -> {
                    setOtherContact(chat);
                    view.markContactAsFriend(
                            Mapper.toContactViewModel(chat.getOtherContact()));
                });

        Subscription searchResultSub = searchRequestSubject
                .debounce(TYPING_EVENT_WAIT_MS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    view.showLoading();
                    service.contactsService().searchContacts(query);
                });

        subscriptions.add(searchResultSub);
        subscriptions.add(contactsQuerySub);
        subscriptions.add(newChatSub);
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
    public void onQueryEntered(String query) {
        if (query.length() < 2) {
            return;
        }

        searchRequestSubject.onNext(query);
    }

    @Override
    public void onContactSendRequestClick(ContactViewModel contact) {
        if (!contact.isFriend() && !contact.getId().equals(userProvider.getCurrentUser().getUId())) {
            Log.d(TAG, "Performing add contact query");
            service.contactsService().addContact(contact.getId());
        }
    }
}
