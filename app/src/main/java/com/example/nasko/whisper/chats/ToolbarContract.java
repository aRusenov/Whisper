package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.BasePresenter;
import com.example.nasko.whisper.BaseView;

public interface ToolbarContract {

    interface View extends BaseView<Presenter> {

        void setNetworkStatus(int status);

        void navigateToContacts();

        void navigateToSettings();

        void navigateToLoginScreen();
    }

    interface Presenter extends BasePresenter<View> {

        void onContactsClicked();

        void onSettingsClicked();

        void onLogoutClicked();
    }
}
