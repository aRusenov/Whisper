package com.example.nasko.whisper.utils;

import com.example.nasko.whisper.models.dto.Chat;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.dto.Message;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    public static MessageViewModel toMessageViewModel(Message message) {
        return new MessageViewModel(message.getText(),
                message.getCreatedAt(),
                toContactViewModel(message.getAuthor()));
    }

    public static ContactViewModel toContactViewModel(Contact contact) {
        return new ContactViewModel(contact.getId(),
                contact.getUsername(),
                contact.getName(),
                contact.getImage(),
                contact.isOnline());
    }

    public static List<MessageViewModel> toMessageViewModelList(List<Message> messages) {
        List<MessageViewModel> msgs = new ArrayList<>(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            msgs.add(Mapper.toMessageViewModel(messages.get(i)));
        }

        return msgs;
    }

    public static List<ChatViewModel> toChatViewModelList(Chat[] chats) {
        List<ChatViewModel> result = new ArrayList<>(chats.length);
        for (int i = 0; i < chats.length; i++) {
            result.add(
                    toChatViewModel(chats[i])
            );
        }

        return result;
    }

    public static ChatViewModel toChatViewModel(Chat chat) {
        return new ChatViewModel(chat.getId(), toMessageViewModel(chat.getLastMessage()));
    }
}
