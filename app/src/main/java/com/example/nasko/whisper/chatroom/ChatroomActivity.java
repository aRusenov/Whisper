package com.example.nasko.whisper.chatroom;

import android.os.Bundle;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.utils.Navigator;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.utils.helpers.FragmentHelperUtil;
import com.example.nasko.whisper.models.view.ChatViewModel;

public class ChatroomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        if (savedInstanceState == null) {
            ChatViewModel chat = getIntent().getParcelableExtra(Navigator.EXTRA_CHAT);
            Bundle args = new Bundle();
            args.putParcelable(Navigator.EXTRA_CHAT, chat);

            FragmentHelperUtil.addFragment(this,
                    new ToolbarFragment(),
                    R.id.container_toolbar_fragment,
                    args,
                    null);

            FragmentHelperUtil.addFragment(this,
                    new ChatroomFragment(),
                    R.id.container_chatroom_fragment,
                    args,
                    null);
        }
    }
}
