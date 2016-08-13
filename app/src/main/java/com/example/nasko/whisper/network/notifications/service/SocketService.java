package com.example.nasko.whisper.network.notifications.service;

public interface SocketService {

    ConnectionService connectionService();

    ContactsService contactsService();

    MessagesService messageService();
}
