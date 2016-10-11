package com.example.nasko.whisper.chatroom;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface ToolbarContract {

    interface View extends BaseView<Presenter> {

        void setContactStatus(boolean online);
    }

    interface Presenter extends BasePresenter { }
}
