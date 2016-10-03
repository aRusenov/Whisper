package com.example.nasko.whisper.activities;

import android.os.Bundle;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.fragments.ChatroomFragment;
import com.example.nasko.whisper.fragments.ChatroomToolbarFragment;
import com.example.nasko.whisper.helpers.FragmentHelperUtil;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.presenters.Navigator;

public class ChatroomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        ChatViewModel chat = getIntent().getParcelableExtra(Navigator.EXTRA_CHAT);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(Navigator.EXTRA_CHAT, chat);
            FragmentHelperUtil.addFragment(this,
                    new ChatroomToolbarFragment(),
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
