package com.example.nasko.whisper.fragments;

import android.support.v4.app.Fragment;

import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.View;

public abstract class BaseFragment<P extends Presenter> extends Fragment implements View<P> {

    private P presenter;

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public P getPresenter() {
        return presenter;
    }
}
