package com.example.nasko.whisper.chats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chats.adapters.ContactQueryAdapter;
import com.example.nasko.whisper.models.view.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends Fragment implements ContactsContract.View {

    private static final String EXTRA_CONTACTS = "contacts";

    private ContactsContract.Presenter presenter;
    private ContactQueryAdapter contactQueryAdapter;

    @BindView(R.id.edit_query) EditText editSearch;
    @BindView(R.id.rv_new_contacts) RecyclerView rvContacts;
    @BindView(R.id.tv_query_message) TextView tvQueryMessage;
    @BindView(R.id.progress_loading) ProgressBar progressLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContactsPresenter(this,
                WhisperApplication.instance().getServiceBinder(),
                WhisperApplication.instance().getUserProvider());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_contacts, container, false);
        ButterKnife.bind(this, view);

        contactQueryAdapter = new ContactQueryAdapter(getContext());
        contactQueryAdapter.setInvitationIconClickListener(position -> {
            ContactViewModel contact = contactQueryAdapter.getItem(position);
            presenter.onContactSendRequestClick(contact);
        });

        if (savedInstanceState != null) {
            ArrayList<ContactViewModel> contacts = savedInstanceState.getParcelableArrayList(EXTRA_CONTACTS);
            if (contacts != null) {
                for (ContactViewModel contact : contacts) {
                    contactQueryAdapter.add(contact);
                }
            }
        }

        rvContacts.setAdapter(contactQueryAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                presenter.onQueryEntered(query);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void showQueryResults(List<ContactViewModel> contacts) {
        editSearch.setError(null);
        if (contacts.size() == 0) {
            tvQueryMessage.setVisibility(View.VISIBLE);
            tvQueryMessage.setText(R.string.message_no_results);
        } else {
            tvQueryMessage.setVisibility(View.GONE);
        }

        contactQueryAdapter.clear();
        contactQueryAdapter.addAll(contacts);
    }

    @Override
    public void markContactAsFriend(ContactViewModel contact) {
        contactQueryAdapter.setContactToFriend(contact);
    }

    @Override
    public void showLoading() {
        progressLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void displayQueryTooShortError() {
        editSearch.setError(getContext().getString(R.string.error_search_too_short));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<ContactViewModel> savedContacts = new ArrayList<>(contactQueryAdapter.size());
        for (int i = 0; i < savedContacts.size(); i++) {
            savedContacts.add(contactQueryAdapter.getItem(i));
        }

        outState.putParcelableArrayList(EXTRA_CONTACTS, savedContacts);
    }
}
