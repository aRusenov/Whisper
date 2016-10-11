package com.example.nasko.whisper.data.socket.service;

public interface SocketService {

    ConnectionService connectionService();

    ContactsService contactsService();

    MessagesService messageService();
}
