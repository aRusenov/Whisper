package com.example.nasko.whisper.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.nasko.whisper.presenters.ChatsPresenter;
import com.example.nasko.whisper.views.fragments.ChatsFragment;
import com.example.nasko.whisper.views.fragments.ContactsSearchFragment;
import com.example.nasko.whisper.views.fragments.ProfileFragment;

public class MenuFragmentPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = "FragmentPageAdapter";
    private static final int FRAGMENT_COUNT = 3;

    private ChatsPresenter chatsPresenter;

    public MenuFragmentPageAdapter(FragmentManager fm, ChatsPresenter chatsPresenter) {
        super(fm);
        this.chatsPresenter = chatsPresenter;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            {
                Log.d(TAG, "Creating ChatsFragment");
                ChatsFragment fragment = new ChatsFragment();
                fragment.setChatsPresenter(chatsPresenter);
                return fragment;
            }
            case 1:
            {
                Log.d(TAG, "Creating ContactsSearchFragment");
                ContactsSearchFragment fragment = new ContactsSearchFragment();
                fragment.setChatsPresenter(chatsPresenter);
                return fragment;
            }
            case 2:
            {
                return new ProfileFragment();
            }
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
