package com.example.nasko.whisper.chats.di.components;

import com.example.nasko.whisper.chats.ToolbarFragment;
import com.example.nasko.whisper.chats.di.modules.ChatsToolbarPresenterModule;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ChatsToolbarPresenterModule.class)
public interface ChatsToolbarViewComponent {

    void inject(ToolbarFragment fragment);
}
