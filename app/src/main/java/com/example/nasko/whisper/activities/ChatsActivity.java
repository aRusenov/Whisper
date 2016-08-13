package com.example.nasko.whisper.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.presenters.chats.NavBarPresenter;
import com.example.nasko.whisper.presenters.chats.NavBarPresenterImpl;
import com.example.nasko.whisper.views.adapters.MenuFragmentPageAdapter;
import com.example.nasko.whisper.views.contracts.ChatsNavBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsActivity extends AppCompatActivity implements ChatsNavBarView {

    private static final String TAG = ChatsActivity.class.getName();
    private static final int[] TAB_DRAWABLES = new int[] { R.drawable.home, R.drawable.search };

    private NavBarPresenter presenter;

    private MenuFragmentPageAdapter pageAdapter;

    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        ButterKnife.bind(this);

        pageAdapter = new MenuFragmentPageAdapter(getSupportFragmentManager());
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

        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < TAB_DRAWABLES.length; i++) {
            tabLayout.getTabAt(i).setIcon(TAB_DRAWABLES[i]);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new NavBarPresenterImpl();
        presenter.attachView(this, this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "OnPause");
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
        presenter = null;
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
                presenter.onSettingsClicked();
                return true;
            case R.id.action_logout:
                presenter.onLogoutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setNetworkStatus(String message) {
        getSupportActionBar().setTitle(message);
    }
}
