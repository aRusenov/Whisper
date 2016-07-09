package com.example.nasko.whisper.data;

import android.content.Context;

public interface ChatService {

    ChatData getChatData();

    ContactsData getContactsData();

    UserData getUserData();

    void setContext(Context context);

    Context getContext();
}
