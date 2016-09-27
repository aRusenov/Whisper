package com.example.nasko.whisper.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.fragments.ChatroomFragment;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.chats.NavBarPresenter;
import com.example.nasko.whisper.presenters.chats.NavBarPresenterImpl;
import com.example.nasko.whisper.views.contracts.ChatsNavBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<NavBarPresenter> implements ChatsNavBarView, ViewCoordnator {

    private static final String TAG = "MainActivity";
    public static final String FRAGMENT_TAG = "fragment_chatroom";

    private UserProvider userProvider;
    private Navigator navigator;
    private boolean multipane;

    @BindView(R.id.toolbar_main) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        userProvider = WhisperApplication.instance().getUserProvider();
        navigator = WhisperApplication.instance().getNavigator();

        User currentUser = userProvider.getCurrentUser();
        if (currentUser == null || currentUser.getSessionToken() == null) {
            navigator.navigateToLoginScreen(this);
            finish();
            return;
        }

        multipane = findViewById(R.id.container_chatroom_fragment) != null;

        Bundle extras = getIntent().getExtras();
        ChatViewModel chat;
        // Check if user has navigated from notification
        if (extras != null && (chat = extras.getParcelable("chat")) != null) {
            Bundle args = new Bundle();
            args.putParcelable("chat", chat);
            if (multipane) {
                addChatroomFragment(args);
            } else {
                // Not multipane -> navigate to chatroom activtiy
                navigator.navigateToChatroom(this, chat);
                finish();
                return;
            }
        }

        // Normal startup
        setSupportActionBar(toolbar);

        setPresenter(new NavBarPresenterImpl());
        getPresenter().attachView(this, this, getIntent().getExtras());
    }

    private void addChatroomFragment(Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        ChatroomFragment fragment = new ChatroomFragment();
        fragment.setArguments(args);

        if (fm.findFragmentByTag(FRAGMENT_TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.container_chatroom_fragment, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.container_chatroom_fragment, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.bar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getPresenter().onSettingsClicked();
                return true;
            case R.id.action_logout:
                getPresenter().onLogoutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setNetworkStatus(String status) {
        getSupportActionBar().setTitle(status);
    }

    @Override
    public void onChatItemClicked(ChatViewModel chat) {
        if (multipane) {
            Bundle args = new Bundle();
            args.putParcelable("chat", chat);
            addChatroomFragment(args);
        } else {
            navigator.navigateToChatroom(this, chat);
        }
    }
}
