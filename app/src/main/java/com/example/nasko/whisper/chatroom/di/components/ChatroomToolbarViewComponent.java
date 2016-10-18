package com.example.nasko.whisper.chatroom.di.components;

import com.example.nasko.whisper.chatroom.ToolbarFragment;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomToolbarPresenterModule;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ChatroomToolbarPresenterModule.class)
public interface ChatroomToolbarViewComponent {

    void inject(ToolbarFragment fragment);
}
