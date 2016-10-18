package com.example.nasko.whisper.data.socket;

public interface SocketService {

    boolean authenticated();

    void start(String userToken);

    void destroy();

    ConnectionService connectionService();

    ContactsService contactsService();

    MessagesService messageService();
}
