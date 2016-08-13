package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.os.Bundle;

import com.example.nasko.whisper.views.contracts.View;

public abstract class AbstractPresenter<V extends View> implements Presenter<V> {

    protected V view;
    protected Context context;

    @Override
    public void attachView(V view, Context context, Bundle extras) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }

        this.view = view;

        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        this.context = context;
    }

    public void onCreate() {
        // Empty
    }

    public void onPause() {
        // Empty
    }

    public void onResume() {
        // Empty
    }

    public void onDestroy() {
        // Empty
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Empty
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        // Empty
    }

    public void detachView() {
        view = null;
        context = null;
    }
}
