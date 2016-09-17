package com.example.nasko.whisper.views.fragments;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.presenters.chatroom.ChatroomPresenter;
import com.example.nasko.whisper.presenters.chatroom.ChatroomPresenterImpl;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.utils.MessageSeparatorDateFormatter;
import com.example.nasko.whisper.views.adapters.MessageAdapter;
import com.example.nasko.whisper.views.contracts.ChatroomView;
import com.example.nasko.whisper.views.listeners.EndlessUpScrollListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatroomFragment extends Fragment implements ChatroomView {

    private ChatroomPresenter presenter;
    private DateFormatter dateFormatter;
    private User user;
    private Chat chat;

    private LinearLayoutManager layoutManager;
    private EndlessUpScrollListener endlessScrollListener;
    private MessageAdapter adapter;

    private Date today = new Date();

    @BindView(R.id.message_list) RecyclerView messageList;
    @BindView(R.id.progress_loading_bar) ProgressBar loadingBar;
    @BindView(R.id.edit_new_message) EditText messageEdit;
    @BindView(R.id.btn_send_message) ImageButton sendButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormatter = new MessageSeparatorDateFormatter();

        chat = getArguments().getParcelable("chat");
        user = getArguments().getParcelable("user");

        presenter = new ChatroomPresenterImpl();
        if (savedInstanceState != null) {
            presenter.onRestoreInstanceState(savedInstanceState);
        }

        presenter.attachView(this, getContext(), getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);
        ButterKnife.bind(this, view);

        loadingBar.setVisibility(View.VISIBLE);
        adapter = new MessageAdapter(getContext(), user, chat.getId());
        if (savedInstanceState != null) {
            ArrayList<Message> messages = savedInstanceState.getParcelableArrayList("messages");
            adapter.addAll(messages);
            loadingBar.setVisibility(View.INVISIBLE);
        }

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

        messageList.setAdapter(adapter);
        messageList.setLayoutManager(layoutManager);

        endlessScrollListener = new EndlessUpScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                presenter.onScrollToTop();
            }
        };

        messageList.addOnScrollListener(endlessScrollListener);
        sendButton.setOnClickListener(v -> sendMessage());
        this.messageEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage();
                return true;
            }

            return false;
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
        ArrayList<Message> savedMessages = new ArrayList<>();
        for (Message msg : adapter) {
            savedMessages.add(msg);
        }

        outState.putParcelableArrayList("messages", savedMessages);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    @Override
    public void loadMessages(List<Message> messages) {
        loadingBar.setVisibility(View.INVISIBLE);

        if (messages.isEmpty()) {
            return;
        }

        addTimeLabels(messages);
        // Insert messages at top
        adapter.addAllAt(0, messages);

        // Maintain scroll position
        int index = layoutManager.findFirstVisibleItemPosition() + messages.size();
        messageList.scrollToPosition(index);
    }

    private void playNewMessageSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
        r.play();
    }

    private void sendMessage() {
        String text = messageEdit.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        presenter.onMessageSend(text);
        messageEdit.setText("");
    }

    @Override
    public void addMessage(Message message) {
        if (! message.getChatId().equals(chat.getId())) {
            return;
        }

        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        boolean scrollToBottom = lastVisible == adapter.getItemCount() - 1;
        adapter.add(message);

        if (scrollToBottom) {
            messageList.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
        } else if (! user.getUId().equals(message.getAuthor().getId())) {
            playNewMessageSound();
        }
    }

    private void addTimeLabels(List<Message> messages) {
        for (int i = messages.size() - 2; i >= 0; i--) {
            Message prev = messages.get(i + 1);
            Message current = messages.get(i);
            // If message is posted on different date than previous -> add a dummy message as separator
            if (prev.getCreatedAt().getDay() != current.getCreatedAt().getDay()) {
                String dateString = dateFormatter.getStringFormat(getContext(), current.getCreatedAt());
                Message label = Message.createDummy(dateString);
                messages.add(i + 1, label);
            }
        }
    }
}
