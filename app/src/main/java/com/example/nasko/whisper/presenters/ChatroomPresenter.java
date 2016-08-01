package com.example.nasko.whisper.presenters;

import android.os.Bundle;

import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.views.contracts.ChatroomView;

public interface ChatroomPresenter extends Presenter {

    void onTakeChatroomView(ChatroomView view, Chat chat);

    void onMessageSend(String text);

    void onScrollToTop();

    void onSaveInstanceState(Bundle outBundle);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onPause();

    void onResume();

    void onDestroy();
}
