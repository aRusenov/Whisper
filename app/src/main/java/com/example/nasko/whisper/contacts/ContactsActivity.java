package com.example.nasko.whisper.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.utils.helpers.FragmentHelperUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsActivity extends AppCompatActivity {

    private static final String FRAGMENT_TAG = "contacts_fragment";

    private SearchViewListener searchViewListener;

    @BindView(R.id.toolbar) Toolbar toolbar;

    public static Intent prepareIntent(Context context) {
        return new Intent(context, ContactsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ButterKnife.bind(this);
        WhisperApplication.baseComponent().inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);

        if (savedInstanceState == null) {
            FragmentHelperUtil.addFragment(this,
                    new ContactsFragment(),
                    R.id.container_contacts_fragment,
                    null, FRAGMENT_TAG);
        }

        searchViewListener = (SearchViewListener) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_contacts_bar, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_show_search).getActionView();
        searchView.setQueryHint(getString(R.string.placeholder_search_contacts));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchViewListener.onSearchQueryChanged(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        return true;
    }
}
