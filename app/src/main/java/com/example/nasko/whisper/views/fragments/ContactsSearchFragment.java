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
import com.example.nasko.whisper.presenters.ChatsPresenter;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.views.adapters.ContactQueryAdapter;
import com.example.nasko.whisper.views.contracts.ContactsSearchView;

import java.util.List;

public class ContactsSearchFragment extends Fragment implements ContactsSearchView {

    private ChatsPresenter chatsPresenter;
    private ContactQueryAdapter contactQueryAdapter;

    private EditText editSearch;
    private RecyclerView rvContacts;
    private TextView tvQueryMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatsPresenter = PresenterCache.instance().getPresenter("Chats", null);
        chatsPresenter.onTakeContactsSearchView(this);

        View view = inflater.inflate(R.layout.fragment_search_contacts, container, false);
        editSearch = (EditText) view.findViewById(R.id.edit_query);
        rvContacts = (RecyclerView) view.findViewById(R.id.rv_new_contacts);
        tvQueryMessage = (TextView) view.findViewById(R.id.tv_query_message);

        contactQueryAdapter = new ContactQueryAdapter(getContext(), chatsPresenter);
        contactQueryAdapter.setInvitationIconClickListener(position -> {
            Contact contact = contactQueryAdapter.getItem(position);
            chatsPresenter.onContactSendRequestClick(contact);
        });

        rvContacts.setAdapter(contactQueryAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                chatsPresenter.onQueryEntered(query);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    public void setChatsPresenter(ChatsPresenter presenter) {
        this.chatsPresenter = presenter;
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
}
