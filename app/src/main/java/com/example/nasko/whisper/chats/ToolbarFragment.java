package com.example.nasko.whisper.chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.chats.di.modules.ChatsToolbarPresenterModule;
import com.example.nasko.whisper.editprofile.ProfileActivity;
import com.example.nasko.whisper.login.LoginActivity;
import com.example.nasko.whisper.models.User;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToolbarFragment extends Fragment implements ToolbarContract.View {

    @Inject ToolbarContract.Presenter presenter;
    private ActionBar actionBar;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        WhisperApplication.userComponent()
                .plus(new ChatsToolbarPresenterModule(this))
                .inject(this);
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

    @Override
    public void navigateToSettings(User user) {
        Intent intent = ProfileActivity.prepareIntent(getActivity());
        startActivity(intent);
    }

    @Override
    public void navigateToLoginScreen() {
        WhisperApplication.releaseUserComponent();

        Intent launchIntent = new Intent(getActivity(), LoginActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchIntent);
    }
}
