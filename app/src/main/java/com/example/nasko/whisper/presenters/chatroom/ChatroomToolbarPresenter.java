package com.example.nasko.whisper.presenters.chatroom;

import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.presenters.Presenter;
import com.example.nasko.whisper.views.contracts.ChatroomNavBarView;

public interface ChatroomToolbarPresenter extends Presenter<ChatroomNavBarView> {

    void updateContactStatus(ContactViewModel contact);
}
