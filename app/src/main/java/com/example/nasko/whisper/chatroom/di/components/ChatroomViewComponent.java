package com.example.nasko.whisper.chatroom.di.components;

import com.example.nasko.whisper.chatroom.ChatroomFragment;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomPresenterModule;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ChatroomPresenterModule.class)
public interface ChatroomViewComponent {

    void inject(ChatroomFragment fragment);
}
