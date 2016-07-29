package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.presenters.ChatroomPresenter;
import com.example.nasko.whisper.presenters.ChatroomPresenterImpl;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.presenters.PresenterFactory;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.utils.MessageSeparatorDateFormatter;
import com.example.nasko.whisper.views.adapters.MessageAdapter;
import com.example.nasko.whisper.views.contracts.ChatroomView;
import com.example.nasko.whisper.views.listeners.EndlessUpScrollListener;

import java.util.Date;
import java.util.List;

public class ChatroomActivity extends AppCompatActivity implements ChatroomView {

    private ChatroomPresenter presenter;
    private PresenterFactory<ChatroomPresenter> presenterFactory = () -> new ChatroomPresenterImpl();
    private DateFormatter dateFormatter;
    private User currentUser;

    private EditText messageEdit;
    private LinearLayoutManager layoutManager;
    private RecyclerView messageList;
    private EndlessUpScrollListener endlessScrollListener;
    private MessageAdapter adapter;
    private String chatId;
    private Date today = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = this.getIntent();
        this.chatId = intent.getStringExtra("chatId");

        presenter = PresenterCache.instance().getPresenter("Chatroom", presenterFactory);

        presenter.onTakeChatroomView(this, chatId);

        dateFormatter = new MessageSeparatorDateFormatter();
        currentUser = WhisperApplication.instance().getCurrentUser();

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        messageList = (RecyclerView) this.findViewById(R.id.message_list);
        adapter = new MessageAdapter(this, currentUser, chatId);
        layoutManager = new LinearLayoutManager(this);
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


        // Request initial messages
//        this.messagesService.loadMessages(chatId, -1, PAGE_SIZE * 2);

        this.messageEdit = (EditText) this.findViewById(R.id.edit_newMessage);
        ImageButton sendButton = (ImageButton) this.findViewById(R.id.btn_send_message);
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
    }

    private void addTimeLabels(List<Message> messages) {
        for (int i = messages.size() - 2; i >= 0; i--) {
            Message prev = messages.get(i + 1);
            Message current = messages.get(i);
            // If message is posted on different date than previous -> add a dummy message as separator
            if (prev.getDate().getDay() != current.getDate().getDay()) {
                String dateString = dateFormatter.getStringFormat(today, current.getDate());
                Message label = Message.createDummy(dateString);
                messages.add(i + 1, label);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void playNewMessageSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
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
        if (! message.getChatId().equals(chatId)) {
            return;
        }

        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        boolean scrollToBottom = lastVisible == adapter.getItemCount() - 1;
        adapter.add(message);

        if (scrollToBottom) {
            messageList.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
        } else if (! currentUser.getUId().equals(message.getFrom())) {
            playNewMessageSound();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void loadMessages(List<Message> messages) {
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
}
