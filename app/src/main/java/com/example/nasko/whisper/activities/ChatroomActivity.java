package com.example.nasko.whisper.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.fragments.ChatroomToolbarFragment;
import com.example.nasko.whisper.fragments.ChatroomFragment;
import com.example.nasko.whisper.models.view.ChatViewModel;

public class ChatroomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        ChatViewModel chat = getIntent().getParcelableExtra("chat");
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable("chat", chat);
            addFragment(new ChatroomFragment(), R.id.container_chatroom_fragment, args);
            addFragment(new ChatroomToolbarFragment(), R.id.container_toolbar_fragment, args);
        }
    }

    private void addFragment(Fragment fragment, int containerId, Bundle args) {
        fragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .add(containerId, fragment)
                .commit();
    }
}
