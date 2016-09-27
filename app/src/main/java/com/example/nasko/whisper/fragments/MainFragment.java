package com.example.nasko.whisper.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.views.adapters.MenuFragmentPageAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends BaseFragment {

    private static final int[] TAB_DRAWABLES = new int[] { R.drawable.home, R.drawable.search };

    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        MenuFragmentPageAdapter pageAdapter = new MenuFragmentPageAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0 && getActivity().getCurrentFocus() != null) {
                    // Hide keyboard if coming from ContactsSearchFragment
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < TAB_DRAWABLES.length && i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(TAB_DRAWABLES[i]);
            }
        }

        return view;
    }
}
