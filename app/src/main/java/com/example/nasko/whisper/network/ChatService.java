package com.example.nasko.whisper.network;

import android.content.Context;

public interface ChatService {

    ChatData getChatData();

    ContactsData getContactsData();

    UserData getUserData();

    void setContext(Context context);

    Context getContext();
}
