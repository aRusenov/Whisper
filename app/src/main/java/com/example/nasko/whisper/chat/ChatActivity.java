package com.example.nasko.whisper.chat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.MainActivity;
import com.example.nasko.whisper.Message;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.MessagesEventListener;
import com.example.nasko.whisper.data.nodejs.NodeJsChatData;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 10;

    private User currentUser;
    private ChatData chatData;
    private EditText messageEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.chatData = NodeJsService.getInstance().getChatData();
        this.currentUser = NodeJsService.getInstance().getUserData().getCurrentUser();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = this.getIntent();
        final String chatId = intent.getStringExtra("chatId");

        final ListView messageList = (ListView) this.findViewById(R.id.message_list);
        final ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, R.layout.chat_message_layout) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Message message = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_layout, parent, false);
                }

                TextView messageView = (TextView) convertView.findViewById(R.id.messageText);
                messageView.setText(message.getText() + "        ");

                TextView messageDate = (TextView) convertView.findViewById(R.id.message_date);
                Date date = message.getDate();
                String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
                messageDate.setText(dateString);

                View offsetView = convertView.findViewById(R.id.offSet_view);
                boolean isMyMessage = message.getFrom().equals(currentUser.getUId());
                int visibility = isMyMessage ? View.VISIBLE : View.GONE;
                int bgDrawable = isMyMessage ? R.drawable.blue_bg : R.drawable.white_bg;
                offsetView.setVisibility(visibility);
                messageView.setBackgroundResource(bgDrawable);

                return convertView;
            }
        };

        messageList.setAdapter(adapter);
        messageList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int firstMessageSeq) {
                if (firstMessageSeq == 0) {
                    return false;
                }

                Log.i("LOADING", "loading messages");
                chatData.requestMessages(currentUser.getUsername(), chatId, firstMessageSeq, PAGE_SIZE);
                return true;
            }
        });

        this.chatData.setMessagesEventListener(new MessagesEventListener() {
            @Override
            public void onMessageAdded(Message message) {
                if (! message.getChatId().equals(chatId)) {
                    return;
                }

                int lastVisible = messageList.getLastVisiblePosition();
                boolean scrollToBottom = lastVisible == adapter.getCount() - 1;
                adapter.add(message);

                if (scrollToBottom) {
                    messageList.setSelection(adapter.getCount() - 1);
                } else if (! currentUser.getUId().equals(message.getFrom())) {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(ChatActivity.this.getApplicationContext(), notification);
                    r.play();
                }
            }

            @Override
            public void onMessagesLoaded(Message[] messages) {
                int index = messageList.getFirstVisiblePosition() + messages.length;
                View v = messageList.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - messageList.getPaddingTop());

                for (Message msg: messages) {
                    adapter.insert(msg, 0);
                }

                messageList.setSelectionFromTop(index, top);
            }
        });

        // Request initial messages
        this.chatData.requestMessages(currentUser.getUsername(), chatId, -1, PAGE_SIZE * 2);

        this.messageEdit = (EditText) this.findViewById(R.id.edit_newMessage);
        Button sendButton = (Button) this.findViewById(R.id.btn_send_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatId);
            }
        });

        this.messageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage(chatId);
                    return true;
                }

                return false;
            }
        });
    }

    private void sendMessage(String chatId) {
        String text = this.messageEdit.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        this.chatData.sendMessage(currentUser.getUsername(), chatId, text);
        this.messageEdit.setText("");
    }
}
