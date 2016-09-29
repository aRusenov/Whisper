package com.example.nasko.whisper.views.contracts;

import com.example.nasko.whisper.presenters.chatroom.ChatroomToolbarPresenter;

public interface ChatroomNavBarView extends View<ChatroomToolbarPresenter> {

    void setContactStatus(boolean online);
}
