package com.example.nasko.whisper.chats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolbarFragment extends Fragment implements ToolbarContract.View {

    private ToolbarContract.Presenter presenter;
    private ActionBar actionBar;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        presenter = new ToolbarPresenter(this,
                getContext(),
                WhisperApplication.instance().getSocketService(),
                WhisperApplication.instance().getUserProvider(),
                WhisperApplication.instance().getNavigator());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_toolbar, container, false);
        ButterKnife.bind(this, view);

        AppCompatActivity host = (AppCompatActivity) getActivity();
        host.setSupportActionBar(toolbar);
        actionBar = host.getSupportActionBar();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ChatToolbar", "onStart");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bar_actions, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
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
    public void setNetworkStatus(String status) {
        actionBar.setTitle(status);
    }
}
