package com.example.nasko.whisper.chatroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.FragmentHelperUtil;

public class ChatroomActivity extends BaseActivity {

    private static final String EXTRA_CHAT = "chat";

    public static Intent prepareIntent(Context launchContext, ChatViewModel chat) {
        Intent intent = new Intent(launchContext, ChatroomActivity.class);
        intent.putExtra(EXTRA_CHAT, chat);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        WhisperApplication.baseComponent().inject(this);

        if (savedInstanceState == null) {
            ChatViewModel chat = getIntent().getParcelableExtra(EXTRA_CHAT);

            Bundle toolbarArgs = new Bundle();
            toolbarArgs.putParcelable(ToolbarFragment.EXTRA_CHAT, chat);
            FragmentHelperUtil.addFragment(this,
                    new ToolbarFragment(),
                    R.id.container_toolbar_fragment,
                    toolbarArgs,
                    null);

            Bundle chatroomArgs = new Bundle();
            chatroomArgs.putParcelable(ChatroomFragment.EXTRA_CHAT, chat);
            FragmentHelperUtil.addFragment(this,
                    new ChatroomFragment(),
                    R.id.container_chatroom_fragment,
                    chatroomArgs,
                    null);
        }
    }
}
