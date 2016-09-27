package com.example.nasko.whisper.activities;

import android.os.Bundle;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.fragments.ChatroomFragment;

public class ChatroomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        if (savedInstanceState == null) {
            ChatroomFragment fragment = new ChatroomFragment();
            Bundle args = new Bundle();
            args.putParcelable("chat", getIntent().getParcelableExtra("chat"));
            fragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_chatroom_fragment, fragment)
                    .commit();
        }
    }
}
