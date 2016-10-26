package com.example.nasko.whisper.contacts;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.BaseView;

import java.util.List;

public interface ContactsContract {

    interface View extends BaseView<Presenter> {

        void showQueryResults(List<ContactViewModel> contacts);

        void markContactAsFriend(ContactViewModel contact);

        void showLoading();

        void hideLoading();

        void displayQueryTooShortError();
    }

    interface Presenter extends BasePresenter {

        void onQueryEntered(String query);

        void onContactSendRequestClick(ContactViewModel contact);
    }
}
