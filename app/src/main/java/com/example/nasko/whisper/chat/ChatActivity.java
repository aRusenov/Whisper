package com.example.nasko.whisper.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.ChatData;
import com.example.nasko.whisper.data.nodejs.NodeJsService;

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

        final ListView messageList = (ListView) this.findViewById(R.id.message_list);
        final MessageAdapter adapter = new MessageAdapter(this, R.layout.chat_message_layout, messageList, currentUser, chatId);

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

        this.chatData.setMessagesEventListener(adapter);

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

    private void sendMessage() {
        String text = this.messageEdit.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        this.chatData.sendMessage(currentUser.getUsername(), chatId, text);
        this.messageEdit.setText("");
    }
}
