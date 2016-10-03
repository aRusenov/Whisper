package com.example.nasko.whisper.helpers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class FragmentHelperUtil {

    public static void addFragment(AppCompatActivity activity, Fragment fragment, int containerId, Bundle args, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fragment.setArguments(args);

        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
                    .add(containerId, fragment, tag)
                    .commit();
        }
    }

    public static void addOrReplaceFragment(AppCompatActivity activity, Fragment fragment, int containerId, Bundle args, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        fragment.setArguments(args);

        if (fm.findFragmentByTag(tag) == null) {
            fm.beginTransaction()
                    .add(containerId, fragment, tag)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(containerId, fragment, tag)
                    .commit();
        }
    }
}
