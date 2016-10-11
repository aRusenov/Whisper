package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.ServiceBoundPresenter;
import com.example.nasko.whisper.utils.helpers.Mapper;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.data.socket.consumer.SocketServiceBinder;
import com.example.nasko.whisper.data.socket.service.SocketService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ContactsPresenter extends ServiceBoundPresenter implements ContactsContract.Presenter {

    private static final String TAG = ContactsPresenter.class.getName();
    private static final long TYPING_EVENT_WAIT_MS = 500;

    private ContactsContract.View view;
    private PublishSubject<String> searchRequestSubject = PublishSubject.create();

    public ContactsPresenter(ContactsContract.View view, SocketServiceBinder serviceBinder,
                             UserProvider userProvider) {
        super(serviceBinder, userProvider);
        this.view = view;
    }

    @Override
    public void onServiceBind(SocketService service, CompositeSubscription serviceSubscriptions) {
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
                    if (query.length() == 0) {
                        return;
                    }

                    if (query.length() < 2) {
                        view.displayQueryTooShortError();
                        return;
                    }

                    view.showLoading();
                    service.contactsService().searchContacts(query);
                });

        serviceSubscriptions.add(searchResultSub);
        serviceSubscriptions.add(contactsQuerySub);
        serviceSubscriptions.add(newChatSub);
    }

    @Override
    public void onServiceUnbind() { }

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
