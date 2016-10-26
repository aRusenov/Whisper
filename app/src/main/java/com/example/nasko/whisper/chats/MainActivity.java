package com.example.nasko.whisper.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chatroom.ChatroomActivity;
import com.example.nasko.whisper.chatroom.ChatroomFragment;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.FragmentHelperUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ViewCoordinator {

    private static final String FRAGMENT_CHATROOM_TAG = "fragment_chatroom";
    private static final String EXTRA_CHAT = "chat";

    private boolean multipane;

    // Available only in multi-pane layout
    @Nullable @BindView(R.id.tv_empty_chatroom) TextView tvEmptyChatroom;

    public static Intent prepareIntent(@NonNull Context context, @Nullable ChatViewModel chat) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_CHAT, chat);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        WhisperApplication.baseComponent().inject(this);
        multipane = findViewById(R.id.container_chatroom_fragment) != null;

        Bundle extras = getIntent().getExtras();
        ChatViewModel chat;
        // Check if user has navigated from notification
        if (extras != null && (chat = extras.getParcelable(EXTRA_CHAT)) != null) {
            handleChatNavigation(chat);
        }

        // Normal startup
        if (savedInstanceState == null) {
            FragmentHelperUtil.addFragment(this,
                    new com.example.nasko.whisper.chats.ToolbarFragment(),
                    R.id.container_toolbar_chats_fragment,
                    null, null);

            FragmentHelperUtil.addFragment(this,
                    new ChatsFragment(),
                    R.id.container_chats_fragment,
                    null, null);
        }
    }

    @Override
    public void onChatItemClicked(ChatViewModel chat) {
        handleChatNavigation(chat);
    }

    private void handleChatNavigation(ChatViewModel chat) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CHAT, chat);
        if (multipane) {
            if (tvEmptyChatroom != null && tvEmptyChatroom.getVisibility() != View.GONE) {
                tvEmptyChatroom.setVisibility(View.GONE);
            }

            FragmentHelperUtil.addOrReplaceFragment(this,
                    new ChatroomFragment(),
                    R.id.container_chatroom_fragment,
                    args,
                    FRAGMENT_CHATROOM_TAG);
        } else {
            // Not multipane -> simply navigate to ChatroomActivtiy
            Intent intent = ChatroomActivity.prepareIntent(this, chat);
            startActivity(intent);
        }
    }
}
