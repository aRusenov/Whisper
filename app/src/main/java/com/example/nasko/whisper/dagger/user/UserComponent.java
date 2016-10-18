package com.example.nasko.whisper.dagger.user;

import com.example.nasko.whisper.chatroom.di.modules.ChatroomPresenterModule;
import com.example.nasko.whisper.chatroom.di.modules.ChatroomToolbarPresenterModule;
import com.example.nasko.whisper.chatroom.di.components.ChatroomToolbarViewComponent;
import com.example.nasko.whisper.chatroom.di.components.ChatroomViewComponent;
import com.example.nasko.whisper.chats.di.modules.ChatsPresenterModule;
import com.example.nasko.whisper.chats.di.modules.ChatsToolbarPresenterModule;
import com.example.nasko.whisper.chats.di.components.ChatsToolbarViewComponent;
import com.example.nasko.whisper.chats.di.components.ChatsViewComponent;
import com.example.nasko.whisper.chats.di.modules.ContactsPresenterModule;
import com.example.nasko.whisper.chats.di.components.ContactsViewComponent;

import dagger.Subcomponent;

@UserScope
@Subcomponent(modules = SocketModule.class)
public interface UserComponent {

    ChatsToolbarViewComponent plus(ChatsToolbarPresenterModule module);

    ContactsViewComponent plus(ContactsPresenterModule module);

    ChatsViewComponent plus(ChatsPresenterModule module);

    ChatroomViewComponent plus(ChatroomPresenterModule module);

    ChatroomToolbarViewComponent plus(ChatroomToolbarPresenterModule module);
}
