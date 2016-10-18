package com.example.nasko.whisper.chats.di.components;

import com.example.nasko.whisper.chats.ChatsFragment;
import com.example.nasko.whisper.chats.di.modules.ChatsPresenterModule;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ChatsPresenterModule.class)
public interface ChatsViewComponent {

    void inject(ChatsFragment fragment);
}
