package com.example.nasko.whisper.presenters;

import android.support.annotation.NonNull;

public interface PresenterFactory<T extends Presenter> {

    @NonNull
    T createPresenter();
}