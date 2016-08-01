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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.presenters.ChatroomPresenter;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.presenters.PresenterFactory;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.utils.MessageSeparatorDateFormatter;
import com.example.nasko.whisper.views.adapters.MessageAdapter;
import com.example.nasko.whisper.views.contracts.ChatroomView;
import com.example.nasko.whisper.views.listeners.EndlessUpScrollListener;

import java.util.Date;
import java.util.List;

public class ChatroomFragment extends Fragment implements ChatroomView {

    private ChatroomPresenter presenter;
    private PresenterFactory<ChatroomPresenter> presenterFactory;
    private DateFormatter dateFormatter;
    private User user;
    private Chat chat;

    private EditText messageEdit;
    private LinearLayoutManager layoutManager;
    private RecyclerView messageList;
    private EndlessUpScrollListener endlessScrollListener;
    private MessageAdapter adapter;
    private ProgressBar loadingBar;

    private Date today = new Date();

    public void setPresenterFactory(PresenterFactory<ChatroomPresenter> presenterFactory) {
        this.presenterFactory = presenterFactory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = PresenterCache.instance().getPresenter("Chatroom", presenterFactory);
        dateFormatter = new MessageSeparatorDateFormatter();

        chat = getArguments().getParcelable("chat");
        user = getArguments().getParcelable("user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        adapter = new MessageAdapter(getContext(), user, chat.getId());
        messageList = (RecyclerView) view.findViewById(R.id.message_list);
        messageList.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(layoutManager);

        loadingBar = (ProgressBar) view.findViewById(R.id.progress_loading_bar);
        loadingBar.setVisibility(View.VISIBLE);

        endlessScrollListener = new EndlessUpScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                presenter.onScrollToTop();
            }
        };

        messageList.addOnScrollListener(endlessScrollListener);

        this.messageEdit = (EditText) view.findViewById(R.id.edit_newMessage);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.btn_send_message);
        sendButton.setOnClickListener(v -> sendMessage());

//        this.messageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    sendMessage();
//                    return true;
//                }
//
//                return false;
//            }
//        });


        presenter.onTakeChatroomView(this, chat);
        return view;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
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
                String dateString = dateFormatter.getStringFormat(today, current.getCreatedAt());
                Message label = Message.createDummy(dateString);
                messages.add(i + 1, label);
            }
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable("chat", chat);
//        outState.putParcelable("user", user);
//    }
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            chat = savedInstanceState.getParcelable("chat");
//            user = savedInstanceState.getParcelable("user");
//        }
//    }
}
