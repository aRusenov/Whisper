package com.example.nasko.whisper.chatroom;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chatroom.adapters.MessageAdapter;
import com.example.nasko.whisper.chatroom.misc.EndlessUpScrollListener;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomPresenterModule;
import com.example.nasko.whisper.models.LoadingData;
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
import java.util.Date;
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

    private LinearLayoutManager layoutManager;
    private EndlessUpScrollListener endlessScrollListener;
    private MessageAdapter adapter;

    private boolean typing;
    private ChatViewModel chat;
    private ContactViewModel userContact;

    @BindView(R.id.rv_messages) RecyclerView messageList;
    @BindView(R.id.progress_loading) ProgressBar loadingBar;
    @BindView(R.id.edit_new_message) EditText messageEdit;
    @BindView(R.id.btn_send_message) ImageButton btnSend;
    @BindView(R.id.tv_error) TextView tvError;

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

        User user = userProvider.getCurrentUser();
        userContact = new ContactViewModel(user.getUId(), user.getUsername(), user.getName(), user.getImage(), false);
        dateFormatter = new MessageSeparatorDateFormatter();

        presenter.init();
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
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        adapter = new MessageAdapter(getActivity(), userContact, chat.getId(), width);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Without the line below the view scrolls down whenever something is inserted (only on multi-pane mode)
        // More info: https://code.google.com/p/android/issues/detail?id=203574
        layoutManager.setAutoMeasureEnabled(false);

        messageList.setLayoutManager(layoutManager);
        messageList.setAdapter(adapter);
        endlessScrollListener = new EndlessUpScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                boolean loading = presenter.onScrollToTop();
                if (loading) {
                    adapter.add(0, new LoadingData());
                }
            }
        };

        messageList.addOnScrollListener(endlessScrollListener);
        btnSend.setOnClickListener(v -> sendMessage());
        messageEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage();
                return true;
            }

            return false;
        });

        messageEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && typing) {
                stopTyping();
            }
        });

        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !typing) {
                    startTyping();
                } else if (s.length() == 0 && typing) {
                    stopTyping();
                }
            }
        });

        if (savedInstanceState != null) {
            // Restore adapter state
            presenter.setLastLoadedMessageId(savedInstanceState.getInt(KEY_LAST_LOADED_MESSAGE_ID)); // TODO: Redundant?
            ArrayList<MessageViewModel> messages = savedInstanceState.getParcelableArrayList(KEY_MESSAGES);
            for (Object msg : messages) {
                adapter.add(msg);
            }

            loadingBar.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<MessageViewModel> savedMessages = new ArrayList<>();
        for (Object msg : adapter) {
            if (msg instanceof MessageViewModel) {
                savedMessages.add((MessageViewModel) msg);
            }
        }

        outState.putParcelableArrayList(KEY_MESSAGES, savedMessages);
        outState.putInt(KEY_LAST_LOADED_MESSAGE_ID, presenter.getLastLoadedMessageId());
    }

    @Override
    public void loadMessages(List<MessageViewModel> messages) {
        // TODO: Remove and use empty view
        loadingBar.setVisibility(View.INVISIBLE);
        if (adapter.size() > 0 && adapter.getItem(0) instanceof LoadingData) {
            adapter.removeAt(0);
        }

        if (messages.isEmpty()) {
            return;
        }

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

        // Insert items at top
        adapter.addAllAt(0, newItems);

        // Maintain scroll position
        int index = layoutManager.findFirstVisibleItemPosition() + newItems.size();
        messageList.scrollToPosition(index);
    }

    @Override
    public void displayTypingStarted(TypingEvent typingEvent) {
        boolean scrollToBottom = isLastItemVisible();
        adapter.add(typingEvent);
        if (scrollToBottom) {
            scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void displayTypingStopped() {
        if (adapter.last() instanceof TypingEvent) {
            boolean scrollToBottom = isLastItemVisible();
            adapter.removeAt(adapter.size() - 1);
            if (scrollToBottom) {
                scrollToPosition(adapter.getItemCount() - 1);
            }
        }
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

        MessageViewModel msg = new MessageViewModel(chat.getId(), text, new Date(), userContact);
        msg.setStatus(MessageStatus.PENDING);
        msg.setUId(System.nanoTime());
        addMessage(msg);

        presenter.onMessageSend(text, msg.getUId());
        messageEdit.setText("");
    }

    @Override
    public void addMessage(MessageViewModel message) {
        int insertPosition = adapter.size();
        if (adapter.last() instanceof TypingEvent) {
            insertPosition--;
        }

        boolean scroll = isLastItemVisible();
        adapter.add(insertPosition, message);
        if (scroll) {
            scrollToPosition(adapter.getItemCount() - 1);
        } else if (!userContact.getId().equals(message.getAuthor().getId())) {
            playNewMessageSound();
        }
    }

    @Override
    public void updateMessageStatus(long identifier, MessageStatus status) {
        // TODO: Extract in adapter
        for (int i = 0; i < adapter.size(); i++) {
            Object item = adapter.getItem(i);
            if (item instanceof MessageViewModel) {
                MessageViewModel msg = (MessageViewModel)item;
                if (msg.getUId() == identifier) {
                    msg.setStatus(status);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    private void stopTyping() {
        typing = false;
        presenter.onStopTyping();
    }

    private void startTyping() {
        typing = true;
        presenter.onStartTyping();
    }

    private boolean isLastItemVisible() {
        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisible == adapter.getItemCount() - 1;
    }

    private void scrollToPosition(int position) {
        messageList.getLayoutManager().scrollToPosition(position);
    }

    private void playNewMessageSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
        r.play();
    }
}
