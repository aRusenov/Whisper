package com.example.nasko.whisper.chats.di.components;

import com.example.nasko.whisper.chats.ContactsFragment;
import com.example.nasko.whisper.chats.di.modules.ContactsPresenterModule;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ContactsPresenterModule.class)
public interface ContactsViewComponent  {

    void inject(ContactsFragment fragment);
}
