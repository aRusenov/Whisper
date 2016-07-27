package com.example.nasko.whisper.network.notifications;

public class MessageTypes {

    // Requests
    public static final int MSG_STOP_SERVICE = 0;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_MESSAGE = 20;
    public static final int MSG_LOAD_MESSAGES = 30;

//    public static final int MSG_UNREGISTER_CLIENT = 2;
//    public static final int MSG_UNREGISTER_CLIENT = 2;
//    public static final int MSG_UNREGISTER_CLIENT = 2;
//    public static final int MSG_UNREGISTER_CLIENT = 2;
//    public static final int MSG_UNREGISTER_CLIENT = 2;


    // Responses
    public static final int MSG_SHOW_CHATS = 3;
    public static final int MSG_NEW_MESSAGE = 4;
    public static final int MSG_AUTHENTICATED = 5;
    public static final int MSG_MESSAGES_LOADED = 6;
    public static final int MSG_CHATS_LOADED = 7;
    public static final int MSG_CHAT_UPDATED = 8;
    public static final int MSG_CHAT_ADDED = 9;
    public static final int MSG_CONTACTS_QUERY_RESULT = 10;
    public static final int MSG_SEARCH_CONTACTS = 11;
    public static final int MSG_ADD_CONTACT = 12;
    public static final int MSG_UNAUTHORIZED = 13;
}
