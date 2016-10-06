package com.example.nasko.whisper.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.fragments.ChatroomFragment;
import com.example.nasko.whisper.fragments.ChatroomToolbarFragment;
import com.example.nasko.whisper.helpers.FragmentHelperUtil;
import com.example.nasko.whisper.managers.UserProvider;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.presenters.Navigator;
import com.example.nasko.whisper.presenters.main.NavBarPresenter;
import com.example.nasko.whisper.presenters.main.NavBarPresenterImpl;
import com.example.nasko.whisper.views.adapters.MenuFragmentPageAdapter;
import com.example.nasko.whisper.views.contracts.ChatsNavBarView;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<NavBarPresenter> implements ChatsNavBarView, ViewCoordinator {

    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_CHATROOM_TAG = "fragment_chatroom";
    private static final String FRAGMENT_TOOLBAR_TAG = "fragment_toolbar";
    private static final int[] TAB_DRAWABLES = new int[] { R.drawable.home, R.drawable.search };

    private Navigator navigator;
    private boolean multipane;

    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    // Available only in multi-pane layout
    @Nullable @BindView(R.id.tv_empty_chatroom) TextView tvEmptyChatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        UserProvider userProvider = WhisperApplication.instance().getUserProvider();
        navigator = WhisperApplication.instance().getNavigator();

        // Check if user is logged in
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
            handleChatNavigation(chat);
        }

        // Normal startup
        setSupportActionBar(toolbar);
        setupViewPager();
        setupTabLayout();

        setPresenter(new NavBarPresenterImpl());
        getPresenter().attachView(this, this, getIntent().getExtras());
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUId());
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < TAB_DRAWABLES.length && i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(TAB_DRAWABLES[i]);
            }
        }
    }

    private void setupViewPager() {
        MenuFragmentPageAdapter pageAdapter = new MenuFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0 && getCurrentFocus() != null) {
                    // Hide keyboard if coming from ContactsSearchFragment
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(status);
        }
    }

    @Override
    public void onChatItemClicked(ChatViewModel chat) {
        handleChatNavigation(chat);
    }

    private void handleChatNavigation(ChatViewModel chat) {
        Bundle args = new Bundle();
        args.putParcelable(Navigator.EXTRA_CHAT, chat);
        if (multipane) {
            if (tvEmptyChatroom != null && tvEmptyChatroom.getVisibility() != View.GONE) {
                tvEmptyChatroom.setVisibility(View.GONE);
            }

            FragmentHelperUtil.addOrReplaceFragment(this,
                    new ChatroomFragment(),
                    R.id.container_chatroom_fragment,
                    args,
                    FRAGMENT_CHATROOM_TAG);

            FragmentHelperUtil.addOrReplaceFragment(this,
                    new ChatroomToolbarFragment(),
                    R.id.container_toolbar_fragment,
                    args,
                    FRAGMENT_TOOLBAR_TAG);
        } else {
            // Not multipane -> navigate to chatroom activtiy
            navigator.navigateToChatroom(this, chat);
        }
    }
}
