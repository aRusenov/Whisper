package com.example.nasko.whisper.presenters;

import android.content.Context;
import android.os.Bundle;

import com.example.nasko.whisper.views.contracts.View;

public interface Presenter<V extends View> {

    void attachView(V view, Context context, Bundle extras);

    void onCreate();

    void onDestroy();

    void onPause();

    void onResume();

    void onSaveInstanceState(Bundle outBundle);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void detachView();
}
