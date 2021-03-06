package com.example.nasko.whisper.chats;

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
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chats.adapters.ChatAdapter;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.chats.di.modules.ChatsPresenterModule;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.LastMessageDateFormatter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsFragment extends Fragment implements ChatsContract.View {

    @Inject ChatsContract.Presenter presenter;
    @Inject UserProvider userProvider;

    private ChatAdapter chatsAdapter;
    private LinearLayoutManager chatsLayoutManager;

    @BindView(R.id.progress_loading) ProgressBar loadingBar;
    @BindView(R.id.rv_chats) RecyclerView contactsView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewCoordinator viewCoordinator;
        if (getActivity() instanceof ViewCoordinator) {
            viewCoordinator = (ViewCoordinator) getActivity();
        } else {
            throw new IllegalArgumentException("Activity must implement " + ViewCoordinator.class.getSimpleName());
        }

        WhisperApplication.userComponent()
                .plus(new ChatsPresenterModule(this, viewCoordinator))
                .inject(this);

        presenter.init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);

        chatsLayoutManager = new LinearLayoutManager(getActivity());
        chatsAdapter = new ChatAdapter(getContext(), new LastMessageDateFormatter(), userProvider.getCurrentUser());
        chatsAdapter.setItemClickListener(position -> {
            ChatViewModel selectedChat = chatsAdapter.getItem(position);
            presenter.onChatClicked(selectedChat);
        });

        contactsView.setAdapter(this.chatsAdapter);
        contactsView.setLayoutManager(chatsLayoutManager);
//        contactsView.addItemDecoration(new DividerItemDecoration(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsView.scrollToPosition(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
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
