package com.example.nasko.whisper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.activities.ViewCoordinator;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.presenters.chats.ChatsPresenter;
import com.example.nasko.whisper.presenters.chats.ChatsPresenterImpl;
import com.example.nasko.whisper.utils.LastMessageDateFormatter;
import com.example.nasko.whisper.views.adapters.ChatAdapter;
import com.example.nasko.whisper.views.contracts.ChatsView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsFragment extends BaseFragment<ChatsPresenter> implements ChatsView {

    private  static final String TAG = "ChatsFragment";

    private ChatAdapter chatsAdapter;
    private LinearLayoutManager chatsLayoutManager;
    private ViewCoordinator viewCoordinator;

    @BindView(R.id.progress_loading) ProgressBar loadingBar;
    @BindView(R.id.rv_chats) RecyclerView contactsView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewCoordinator) {
            viewCoordinator = (ViewCoordinator) context;
        } else {
            throw new IllegalArgumentException("Activity must implement " + ViewCoordinator.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewCoordinator = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPresenter(new ChatsPresenterImpl());
        getPresenter().attachView(this, getContext(), null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);

        chatsLayoutManager = new LinearLayoutManager(getActivity());
        chatsAdapter = new ChatAdapter(getContext(), new LastMessageDateFormatter());
        chatsAdapter.setItemClickListener(position -> {
            ChatViewModel selectedChat = chatsAdapter.getItem(position);
            getPresenter().onChatClicked(selectedChat);
            viewCoordinator.onChatItemClicked(selectedChat);
        });

        contactsView.setAdapter(this.chatsAdapter);
        contactsView.setLayoutManager(chatsLayoutManager);
//        contactsView.addItemDecoration(new DividerItemDecoration(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsView.scrollToPosition(0);
    }

    @Override
    public void loadChats(List<ChatViewModel> chats) {
        loadingBar.setVisibility(View.GONE);
        chatsAdapter.addAll(chats);
    }

    @Override
    public void addChat(ChatViewModel chat) {
        chatsAdapter.add(0, chat);
        scrollToTopIfVisible();
    }

    @Override
    public void updateChatLastMessage(String chatId, MessageViewModel message) {
        int pos = chatsAdapter.findIndexById(chatId);
        if (pos != -1) {
            ChatViewModel chat = chatsAdapter.getItem(pos);
            chat.setLastMessage(message);
            chatsAdapter.notifyItemChanged(pos);
            chatsAdapter.moveToTop(pos);
        }

        scrollToTopIfVisible();
    }

    @Override
    public void clearChats() {
        chatsAdapter.clear();
    }

    @Override
    public void setChatStatus(String chatId, boolean online) {
        int pos = chatsAdapter.findIndexById(chatId);
        if (pos != -1) {
            ChatViewModel chat = chatsAdapter.getItem(pos);
            chat.getDisplayContact().setOnline(online);
            chatsAdapter.notifyItemChanged(pos);
        }
    }

    private void scrollToTopIfVisible() {
        if (chatsLayoutManager.findFirstVisibleItemPosition() == 0) {
            contactsView.scrollToPosition(0);
        }
    }
}
