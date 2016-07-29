package com.example.nasko.whisper.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.presenters.ChatsPresenter;
import com.example.nasko.whisper.presenters.ChatsPresenterImpl;
import com.example.nasko.whisper.presenters.PresenterCache;
import com.example.nasko.whisper.presenters.PresenterFactory;
import com.example.nasko.whisper.utils.DateProvider;
import com.example.nasko.whisper.views.adapters.MenuFragmentPageAdapter;
import com.example.nasko.whisper.views.contracts.ChatsViewNavigator;

import java.util.Date;

public class ChatsActivity extends AppCompatActivity implements DateProvider, ChatsViewNavigator {

    private static final String TAG = "ChatsActivity";
    private static final String KEY_CHAT_ID = "chatId";
    private static final int[] TAB_DRAWABLES = new int[] { R.drawable.home, R.drawable.search, R.drawable.settings };

    private ChatsPresenter chatsPresenter;
    private PresenterFactory<ChatsPresenter> presenterFactory = () -> new ChatsPresenterImpl();

    private ViewPager viewPager;
    private MenuFragmentPageAdapter pageAdapter;
    private Date today = new Date();

    private boolean isBeingDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Log.d(TAG, "Creating activity");
        chatsPresenter = PresenterCache.instance().getPresenter("Chats", presenterFactory);
        chatsPresenter.setContext(getApplicationContext());
        chatsPresenter.onTakeChatsViewNavigator(this);
        chatsPresenter.onCreate();

        pageAdapter = new MenuFragmentPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(2);

        setToolbar();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < TAB_DRAWABLES.length; i++) {
            tabLayout.getTabAt(i).setIcon(TAB_DRAWABLES[i]);
        }
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setBackground(new ColorDrawable(Color.parseColor("#26A69A")));
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatsPresenter.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.bar_actions, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming activity");
        this.today = new Date();
        chatsPresenter.onResume();
        isBeingDestroyed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isBeingDestroyed = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying activity");
        chatsPresenter.onDestroy();
        if (isBeingDestroyed) {
            PresenterCache.instance().removePresenter("Chats");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                chatsPresenter.onLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Date getDate() {
        return this.today;
    }

    @Override
    public void setNetworkStatus(String message) {
        getSupportActionBar().setTitle(message);
    }

    @Override
    public void navigateToChatroom(Chat chat) {
        Intent intent = new Intent(ChatsActivity.this, ChatroomActivity.class);
        intent.putExtra(KEY_CHAT_ID, chat.getId());
        // TODO: Remove
        intent.putExtra("lastMessageSeq", chat.getLastMessage().getSeq());

        startActivity(intent);
    }

    @Override
    public void navigateToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
