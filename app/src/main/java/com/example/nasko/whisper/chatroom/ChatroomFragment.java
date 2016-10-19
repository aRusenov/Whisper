package com.example.nasko.whisper.chatroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chatroom.adapters.EmojiAdapter;
import com.example.nasko.whisper.chatroom.adapters.MessageAdapter;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomPresenterModule;
import com.example.nasko.whisper.chatroom.views.EndlessUpScrollListener;
import com.example.nasko.whisper.chatroom.views.MessagesRecyclerView;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.MessageSeparator;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.utils.MessageSeparatorDateFormatter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatroomFragment extends Fragment implements ChatroomContract.View {

    public static final String EXTRA_CHAT = "chat";
    private static final String KEY_LAST_LOADED_MESSAGE_ID = "lastLoadedMessageId";
    private static final String KEY_MESSAGES = "messages";
    private static final int DEFAULT_MESSAGE_SEQ = -1;

    @Inject ChatroomContract.Presenter presenter;
    @Inject UserProvider userProvider;
    private DateFormatter dateFormatter;

    private EndlessUpScrollListener endlessScrollListener;

    private boolean typing;
    private ChatViewModel chat;
    private ContactViewModel userContact;

    @BindView(R.id.rv_messages) MessagesRecyclerView messageList;
    @BindView(R.id.rv_emojis) RecyclerView emojisList;
    @BindView(R.id.progress_loading) ProgressBar loadingBar;
    @BindView(R.id.edit_new_message) EditText messageEdit;
    @BindView(R.id.btn_send_message) ImageButton btnSend;
    @BindView(R.id.tv_error) TextView tvError;
    @BindView(R.id.btn_toggle_emojis) ImageButton btnToggleEmojis;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chat = getArguments().getParcelable(EXTRA_CHAT);
        int lastLoadedMessageSeq = savedInstanceState != null ?
                savedInstanceState.getInt(KEY_LAST_LOADED_MESSAGE_ID, DEFAULT_MESSAGE_SEQ) :
                DEFAULT_MESSAGE_SEQ;

        WhisperApplication.userComponent()
                .plus(new ChatroomPresenterModule(this, chat, lastLoadedMessageSeq))
                .inject(this);
        presenter.init();

        User user = userProvider.getCurrentUser();
        userContact = new ContactViewModel(user.getUId(), user.getUsername(), user.getName(), user.getImage(), false);
        dateFormatter = new MessageSeparatorDateFormatter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);
        ButterKnife.bind(this, view);

        loadingBar.setVisibility(View.VISIBLE);
        setupMessagesView();
        setupEmojisView();
        setupMessageEditView();

        if (savedInstanceState != null) {
            ArrayList<MessageViewModel> messages = savedInstanceState.getParcelableArrayList(KEY_MESSAGES);
            loadMessages(messages);
            loadingBar.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void setupMessageEditView() {
        btnSend.setOnClickListener(v -> sendMessage());

        messageEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && typing) {
                stopTyping();
            }
        });

        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !typing) {
                    startTyping();
                } else if (s.length() == 0 && typing) {
                    stopTyping();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void setupMessagesView() {
        MessageAdapter messagesAdapter = new MessageAdapter(getContext(), userContact, getActivityWidth());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // Without the line below the view scrolls down whenever something is inserted (only on multi-pane mode)
        // More info: https://code.google.com/p/android/issues/detail?id=203574
        layoutManager.setAutoMeasureEnabled(false);

        messageList.setAdapter(messagesAdapter);
        messageList.setLayoutManager(layoutManager);
        endlessScrollListener = new EndlessUpScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                boolean loading = presenter.onScrollToTop();
                if (loading) {
                    messageList.addLoading();
                }
            }
        };

        messageList.addOnScrollListener(endlessScrollListener);
    }

    private void setupEmojisView() {
        EmojiAdapter emojiAdapter = new EmojiAdapter(getContext());
        emojiAdapter.addAll(WhisperApplication.getEmojiComponent().emojis());
        emojiAdapter.setItemClickListener(position -> {
            String emoji = emojiAdapter.getItem(position);
            int selectionIndex = messageEdit.getSelectionStart();
            messageEdit.getText().insert(selectionIndex, emoji);
        });

        emojisList.setAdapter(emojiAdapter);
        emojisList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        btnToggleEmojis.setOnClickListener(v -> {
            int visibility = emojisList.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            emojisList.setVisibility(visibility);
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<MessageViewModel> savedMessages = messageList.getMessages();
        outState.putParcelableArrayList(KEY_MESSAGES, savedMessages);
        outState.putInt(KEY_LAST_LOADED_MESSAGE_ID, presenter.getLastLoadedMessageId());
    }

    @Override
    public void loadMessages(List<MessageViewModel> messages) {
        loadingBar.setVisibility(View.INVISIBLE);

        // Add timestamps if necessary
        List<Object> newItems = new ArrayList<>(messages.size() + 5);
        newItems.add(messages.get(0));
        for (int i = 1; i < messages.size(); i++) {
            MessageViewModel prev = messages.get(i - 1);
            MessageViewModel current = messages.get(i);
            // If message is posted on different date than previous -> add a timestamp
            if (prev.getCreatedAt().getDay() != current.getCreatedAt().getDay()) {
                String dateString = dateFormatter.getStringFormat(getContext(), current.getCreatedAt());
                newItems.add(new MessageSeparator(dateString));
            }

            newItems.add(current);
        }

        messageList.addMessagesAtStart(newItems);
    }

    @Override
    public void displayTypingStarted(TypingEvent typingEvent) {
        messageList.addTypingEvent(typingEvent);
    }

    @Override
    public void displayTypingStopped() {
        messageList.removeTypingEvent();
    }

    @Override
    public void displayNetworkError() {
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNetworkError() {
        tvError.setVisibility(View.INVISIBLE);
    }

    private void sendMessage() {
        String text = messageEdit.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        MessageViewModel newMessage = presenter.onMessageSend(text, userContact);
        messageList.addMessageAtEnd(newMessage);
        messageEdit.setText("");
    }

    @Override
    public void addMessage(MessageViewModel message) {
        messageList.addMessageAtEnd(message);
    }

    @Override
    public void updateMessageStatus(long identifier, MessageStatus status) {
        messageList.updateMessageStatus(identifier, status);
    }

    private void stopTyping() {
        typing = false;
        presenter.onStopTyping();
    }

    private void startTyping() {
        typing = true;
        presenter.onStartTyping();
    }

    private int getActivityWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
