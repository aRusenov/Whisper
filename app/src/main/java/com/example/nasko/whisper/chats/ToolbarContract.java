package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;
import com.example.nasko.whisper.models.User;

public interface ToolbarContract {

    interface View extends BaseView<Presenter> {

        void setNetworkStatus(String status);

        void navigateToSettings(User user);

        void navigateToLoginScreen();
    }

    interface Presenter extends BasePresenter {

        void onSettingsClicked();

        void onLogoutClicked();
    }
}
