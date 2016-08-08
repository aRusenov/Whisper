package com.example.nasko.whisper.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.nasko.whisper.views.fragments.ChatsFragment;
import com.example.nasko.whisper.views.fragments.ContactsSearchFragment;

public class MenuFragmentPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = "FragmentPageAdapter";
    private static final int FRAGMENT_COUNT = 2;

    public MenuFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            {
                Log.d(TAG, "Creating ChatsFragment");
                ChatsFragment fragment = new ChatsFragment();
                return fragment;
            }
            case 1:
            {
                Log.d(TAG, "Creating ContactsSearchFragment");
                ContactsSearchFragment fragment = new ContactsSearchFragment();
                return fragment;
            }
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
