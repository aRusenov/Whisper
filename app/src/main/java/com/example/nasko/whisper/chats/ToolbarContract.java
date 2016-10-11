package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface ToolbarContract {

    interface View extends BaseView<Presenter> {

        void setNetworkStatus(String status);
    }

    interface Presenter extends BasePresenter {

        void onSettingsClicked();

        void onLogoutClicked();
    }
}
