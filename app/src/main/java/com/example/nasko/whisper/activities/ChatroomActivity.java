package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.views.fragments.ChatroomFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatroomActivity extends AppCompatActivity {

    private Chat chat;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Intent intent = this.getIntent();
        chat = intent.getParcelableExtra("chat");
        user = intent.getParcelableExtra("user");

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setToolbar();

        if (savedInstanceState == null) {
            ChatroomFragment chatroomFragment = new ChatroomFragment();
            Bundle args = new Bundle();
            args.putParcelable("chat", chat);
            args.putParcelable("user", user);
            chatroomFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, chatroomFragment)
                    .commit();
        }
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (chat.getOtherContact() == null) {
            return;
        }

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            CircleImageView profileImage = (CircleImageView) myToolbar.findViewById(R.id.profile_image);
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) profileImage.getLayoutParams();
            layoutParams.height = (actionBarHeight * 3) / 4;
            layoutParams.width = (actionBarHeight * 3) / 4;

            Picasso.with(this)
                    .load(chat.getOtherContact().getImageUrl())
                    .placeholder(R.drawable.profile)
                    .into(profileImage);

            TextView tvName = (TextView) myToolbar.findViewById(R.id.tv_contact_name);
            String name = chat.getOtherContact().getUsername();
            tvName.setText(name);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 3);

            TextView tvStatus = (TextView) myToolbar.findViewById(R.id.tv_status);
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 4);
        }
    }
}
