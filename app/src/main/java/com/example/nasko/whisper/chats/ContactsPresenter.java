package com.example.nasko.whisper.chats;

import android.util.Log;

import com.example.nasko.whisper.SocketPresenter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class ContactsPresenter extends SocketPresenter implements ContactsContract.Presenter {

    private static final String TAG = "ContactsPresenter";
    private static final long TYPING_EVENT_WAIT_MS = 500;

    private ContactsContract.View view;
    private PublishSubject<String> searchRequestSubject = PublishSubject.create();

    public ContactsPresenter(ContactsContract.View view, SocketService socketService,
                             UserProvider userProvider) {
        super(socketService, userProvider);
        this.view = view;
        initListeners();
    }

    @Override
    public void destroy() {
        super.destroy();
        view = null;
    }

    private void initListeners() {
        Subscription contactsQuerySub = socketService.contactsService()
                .onContactQueryResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    List<ContactViewModel> result = Mapper.toContactViewModelList(response.getContacts());

                    view.showQueryResults(result);
                    view.hideLoading();
                });

        Subscription newChatSub = socketService.contactsService()
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
                    socketService.contactsService().searchContacts(query);
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
        searchRequestSubject.onNext(query);
    }

    @Override
    public void onContactSendRequestClick(ContactViewModel contact) {
        if (!contact.isFriend() && !contact.getId().equals(userProvider.getCurrentUser().getUId())) {
            Log.d(TAG, "Performing add contact query");
            socketService.contactsService().addContact(contact.getId());
        }
    }
}
