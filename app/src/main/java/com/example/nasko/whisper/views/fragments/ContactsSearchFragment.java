package com.example.nasko.whisper.views.fragments;

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
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.presenters.chats.ContactsSearchPresenterImpl;
import com.example.nasko.whisper.presenters.chats.ContactsSearchPresenter;
import com.example.nasko.whisper.views.adapters.ContactQueryAdapter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsSearchFragment extends Fragment implements ContactsSearchView {

    private ContactsSearchPresenter presenter;

    private ContactQueryAdapter contactQueryAdapter;

    @BindView(R.id.edit_query) EditText editSearch;
    @BindView(R.id.rv_new_contacts) RecyclerView rvContacts;
    @BindView(R.id.tv_query_message) TextView tvQueryMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ContactsSearchPresenterImpl();
        presenter.attachView(this, getContext(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_contacts, container, false);
        ButterKnife.bind(this, view);

        contactQueryAdapter = new ContactQueryAdapter(getContext());
        contactQueryAdapter.setInvitationIconClickListener(position -> {
            if (presenter != null) {
                Contact contact = contactQueryAdapter.getItem(position);
                presenter.onContactSendRequestClick(contact);
            }
        });

        if (savedInstanceState != null) {
            ArrayList<Contact> contacts = savedInstanceState.getParcelableArrayList("contacts");
            for (Contact contact : contacts) {
                contactQueryAdapter.add(contact);
            }
        }

        rvContacts.setAdapter(contactQueryAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (presenter != null) {
                    String query = s.toString();
                    presenter.onQueryEntered(query);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public void loadQueryResults(List<Contact> contacts) {
        if (contacts.size() == 0) {
            tvQueryMessage.setVisibility(View.VISIBLE);
            tvQueryMessage.setText("No results");
        } else {
            tvQueryMessage.setVisibility(View.GONE);
        }

        contactQueryAdapter.clear();
        contactQueryAdapter.addAll(contacts);
    }

    @Override
    public void markContactAsFriend(Contact contact) {
        contactQueryAdapter.setContactToFriend(contact);
    }

    @Override
    public void displayInfoText(String text) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Contact> savedContacts = new ArrayList<>();
        for (Contact contact : contactQueryAdapter) {
            savedContacts.add(contact);
        }

        outState.putParcelableArrayList("contacts", savedContacts);
    }
}
