package com.example.nasko.whisper.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.presenters.ChatsPresenter;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.utils.DateProvider;
import com.example.nasko.whisper.views.adapters.ChatAdapter;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.Date;
import java.util.List;

public class ChatsFragment extends Fragment implements DateProvider, ChatsView {

    private  static final String TAG = "ChatsFragment";

    private ChatsPresenter chatsPresenter;

    private ChatAdapter chatsAdapter;
    private RecyclerView contactsView;
    private LinearLayoutManager chatsLayoutManager;
    private ProgressBar loadingBar;
    private Date now = new Date();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        contactsView = (RecyclerView) view.findViewById(R.id.rv_chats);
        loadingBar = (ProgressBar) view.findViewById(R.id.progress_loading);
        chatsAdapter = new ChatAdapter(getContext(), this);
        chatsAdapter.setItemClickListener(position -> {
            Chat selectedChat = chatsAdapter.getItem(position);
            chatsPresenter.onChatClicked(selectedChat);
        });

        contactsView.setAdapter(this.chatsAdapter);
        chatsLayoutManager = new LinearLayoutManager(getActivity());
        contactsView.setLayoutManager(chatsLayoutManager);

        chatsPresenter = PresenterCache.instance().getPresenter("Chats", null);
        chatsPresenter.onTakeChatsView(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setChatsPresenter(ChatsPresenter presenter) {
        this.chatsPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        now = new Date();
        Log.d(TAG, "OnResume called");
        contactsView.scrollToPosition(0);
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
