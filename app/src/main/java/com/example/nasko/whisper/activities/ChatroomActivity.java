package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.presenters.chatroom.ChatroomNavBarPresenter;
import com.example.nasko.whisper.views.contracts.ChatroomNavBarView;
import com.example.nasko.whisper.fragments.ChatroomFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatroomActivity extends BaseActivity implements ChatroomNavBarView {

    private ChatViewModel chat;
    private User user;

    private ChatroomNavBarPresenter presenter;

    @BindView(R.id.my_toolbar) Toolbar myToolbar;
    @BindView(R.id.tv_contact_name) TextView tvName;
    @BindView(R.id.tv_status) TextView tvStatus;
    @BindView(R.id.status_image) ImageView statusImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        ButterKnife.bind(this);

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

        presenter = new ChatroomNavBarPresenter();
        presenter.attachView(this, this, null);
    }

    private void setToolbar() {
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ContactViewModel displayContact = chat.getDisplayContact();
        if (displayContact == null) {
            return;
        }

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            tvName.setText(displayContact.getUsername());
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 3);
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, actionBarHeight / 4);
            setContactStatus(displayContact.isOnline());
        }
    }

    @Override
    public void setContactStatus(boolean online) {
        int statusRes = online ? R.drawable.circle_green : R.drawable.circle_gray;
        statusImg.setImageResource(statusRes);
        tvStatus.setText(online ? getString(R.string.status_online) : getString(R.string.status_offline));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }
}
