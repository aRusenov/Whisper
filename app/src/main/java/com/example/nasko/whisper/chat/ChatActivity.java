package com.example.nasko.whisper.chat;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.nasko.whisper.Message;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.listeners.MessagesEventListener;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 10;

    private User currentUser;
    private ChatData chatData;
    private EditText messageEdit;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.chatData = NodeJsService.getInstance().getChatData();
        this.currentUser = NodeJsService.getInstance().getUserData().getCurrentUser();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = this.getIntent();
        this.chatId = intent.getStringExtra("chatId");

        final RecyclerView messageList = (RecyclerView) this.findViewById(R.id.message_list);
        final MessageAdapter adapter = new MessageAdapter(this, currentUser, chatId);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messageList.setAdapter(adapter);
        messageList.setLayoutManager(layoutManager);

        final EndlessUpScrollListener endlessScrollListener = new EndlessUpScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int lastLoadedItemId) {
                if (lastLoadedItemId == 0) {
                    return;
                }

                Log.i("LOADING", "actually loading now");
                chatData.requestMessages(currentUser.getUsername(), chatId, lastLoadedItemId, PAGE_SIZE);
            }
        };

        messageList.addOnScrollListener(endlessScrollListener);

        this.chatData.setMessagesEventListener(new MessagesEventListener() {
            @Override
            public void onMessageAdded(Message message) {
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
            public void onMessagesLoaded(List<Message> messages) {
                // Insert messages at top
                adapter.addAllAt(0, messages);
                int lastLoadedMessageSeq = messages.get(0).getSeq();
                endlessScrollListener.setLastLoadedItemId(lastLoadedMessageSeq);

                // Maintain scroll position
                int index = layoutManager.findFirstVisibleItemPosition() + messages.size();
                View topmostView = messageList.getChildAt(0);
                int top = (topmostView == null) ? 0 : (topmostView.getTop() - messageList.getPaddingTop());
                messageList.scrollToPosition(index);
            }
        });

        // Request initial messages
        this.chatData.requestMessages(currentUser.getUsername(), chatId, -1, PAGE_SIZE * 2);

        this.messageEdit = (EditText) this.findViewById(R.id.edit_newMessage);
        ImageButton sendButton = (ImageButton) this.findViewById(R.id.btn_send_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

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

    private void playNewMessageSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();
    }

    private void sendMessage() {
        String text = this.messageEdit.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        this.chatData.sendMessage(currentUser.getUsername(), chatId, text);
        this.messageEdit.setText("");
    }
}
