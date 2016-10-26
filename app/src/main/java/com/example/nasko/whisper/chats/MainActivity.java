package com.example.nasko.whisper.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.nasko.whisper.BaseActivity;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chatroom.ChatroomActivity;
import com.example.nasko.whisper.chatroom.ChatroomFragment;
import com.example.nasko.whisper.chats.adapters.MenuFragmentPageAdapter;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.utils.helpers.FragmentHelperUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ViewCoordinator {

    private static final int[] TAB_DRAWABLES = new int[] { R.drawable.home, R.drawable.search };
    private static final String FRAGMENT_CHATROOM_TAG = "fragment_chatroom";
    private static final String FRAGMENT_TOOLBAR_TAG = "fragment_toolbar";
    private static final String EXTRA_CHAT = "chat";

    private boolean multipane;

    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
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
                    R.id.toolbar_container,
                    null, null);
        }

        setupViewPager();
        setupTabLayout();
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
                    // Hide keyboard if coming from ContactsFragment
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
