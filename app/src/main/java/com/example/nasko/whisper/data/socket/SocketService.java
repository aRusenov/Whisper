package com.example.nasko.whisper.data.socket;

public interface SocketService {

    void start(String userToken);

    void destroy();

    ConnectionService connectionService();

    ContactsService contactsService();

    MessagesService messageService();
}
