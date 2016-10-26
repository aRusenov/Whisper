package com.example.nasko.whisper.contacts;

import android.util.Log;

import com.example.nasko.whisper.chats.interactors.ContactsSearchInteractor;
import com.example.nasko.whisper.models.view.ContactViewModel;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ContactsPresenter implements ContactsContract.Presenter {

    private static final String TAG = "ContactsPresenter";
    private static final long TYPING_EVENT_WAIT_MS = 500;

    private ContactsContract.View view;
    private CompositeSubscription subscriptions;
    private ContactsSearchInteractor contactsSearchInteractor;
    private PublishSubject<String> searchRequestSubject = PublishSubject.create();

    public ContactsPresenter(ContactsContract.View view, ContactsSearchInteractor contactsSearchInteractor) {
        this.view = view;
        this.contactsSearchInteractor = contactsSearchInteractor;
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void init() {
        Subscription searchSub = contactsSearchInteractor.onSearchResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    view.showQueryResults(contacts);
                    view.hideLoading();
                });

        Subscription newContactSub = contactsSearchInteractor.onContactAdded()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contact -> {
                    if (contact != null) {
                        view.markContactAsFriend(contact);
                    }
                });

        Subscription searchResultSub = searchRequestSubject
                .debounce(TYPING_EVENT_WAIT_MS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    if (query.length() < 2) {
                        view.displayQueryTooShortError();
                    } else {
                        view.showLoading();
                        contactsSearchInteractor.performSearch(query);
                        Log.d(TAG, "Fetching users starting with: " + query);
                    }
                });

        subscriptions.add(searchSub);
        subscriptions.add(newContactSub);
        subscriptions.add(searchResultSub);

        contactsSearchInteractor.init();
    }

    @Override
    public void start() { }

    @Override
    public void stop() { }

    @Override
    public void destroy() {
        subscriptions.clear();
        contactsSearchInteractor.destroy();
        view = null;
    }

    @Override
    public void onQueryEntered(String query) {
        if (query.length() == 0) {
            return;
        }

        searchRequestSubject.onNext(query);
    }

    @Override
    public void onContactSendRequestClick(ContactViewModel contact) {
        contactsSearchInteractor.addContact(contact);
    }
}
