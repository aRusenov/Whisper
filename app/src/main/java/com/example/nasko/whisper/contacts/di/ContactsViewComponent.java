package com.example.nasko.whisper.contacts.di;

import com.example.nasko.whisper.contacts.ContactsFragment;
import com.example.nasko.whisper.dagger.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ContactsPresenterModule.class)
public interface ContactsViewComponent  {

    void inject(ContactsFragment fragment);
}
