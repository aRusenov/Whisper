package com.example.nasko.whisper.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.presenters.chats.ChatsPresenter;
import com.example.nasko.whisper.presenters.chats.ChatsPresenterImpl;
import com.example.nasko.whisper.utils.DateProvider;
import com.example.nasko.whisper.views.adapters.ChatAdapter;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsFragment extends Fragment implements DateProvider, ChatsView {

    private  static final String TAG = ChatsFragment.class.getName();

    private ChatsPresenter presenter;

    private ChatAdapter chatsAdapter;
    private LinearLayoutManager chatsLayoutManager;
    private Date now = new Date();

    @BindView(R.id.progress_loading) ProgressBar loadingBar;
    @BindView(R.id.rv_chats) RecyclerView contactsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChatsPresenterImpl();
        presenter.attachView(this, getContext(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);

        chatsLayoutManager = new LinearLayoutManager(getActivity());
        chatsAdapter = new ChatAdapter(getContext(), this);
        chatsAdapter.setItemClickListener(position -> {
            Chat selectedChat = chatsAdapter.getItem(position);
            presenter.onChatClicked(selectedChat);
        });

        contactsView.setAdapter(this.chatsAdapter);
        contactsView.setLayoutManager(chatsLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
        now = new Date();
        contactsView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public Date getDate() {
        return this.now;
    }

    @Override
    public void loadChats(List<Chat> chats) {
        loadingBar.setVisibility(View.GONE);
        chatsAdapter.addAll(chats);
    }

    @Override
    public void addChat(Chat chat) {
        chatsAdapter.add(0, chat);
    }

    @Override
    public void updateChat(Chat chat) {
        chatsAdapter.update(chat);
        if (chatsLayoutManager.findFirstVisibleItemPosition() == 0) {
            contactsView.scrollToPosition(0);
        }
    }

    @Override
    public void clearChats() {
        chatsAdapter.clear();
    }
}
