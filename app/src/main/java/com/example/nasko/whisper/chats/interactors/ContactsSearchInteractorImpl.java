package com.example.nasko.whisper.chats.interactors;

import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.data.socket.SocketService;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.dto.Contact;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.utils.helpers.Mapper;

import java.util.List;

import rx.Observable;

public class ContactsSearchInteractorImpl implements ContactsSearchInteractor {

    private SocketService socketService;
    private UserProvider userProvider;
    private User currentUser;

    public ContactsSearchInteractorImpl(SocketService socketService, UserProvider userProvider) {
        this.socketService = socketService;
        this.userProvider = userProvider;
        currentUser = userProvider.getCurrentUser();
    }

    @Override
    public void init() { }

    @Override
    public void destroy() { }

    @Override
    public void performSearch(String query) {
        socketService.contactsService().searchContacts(query);
    }

    @Override
    public Observable<List<ContactViewModel>> onSearchResponse() {
        return socketService.contactsService().onContactQueryResponse()
                .map(queryResponse -> Mapper.toContactViewModelList(queryResponse.getContacts()));
    }

    @Override
    public void addContact(ContactViewModel contact) {
        if (!contact.isFriend() && !contact.getId().equals(userProvider.getCurrentUser().getUId())) {
            socketService.contactsService().addContact(contact.getId());
        }
    }

    @Override
    public Observable<ContactViewModel> onContactAdded() {
        return socketService.contactsService().onNewChat()
                .map(chat -> {
                    List<Contact> participants = chat.getParticipants();
                    for (int i = 0; i < participants.size(); i++) {
                        if (! participants.get(i).getId().equals(currentUser.getUId())) {
                            return Mapper.toContactViewModel(participants.get(i));
                        }
                    }

                    return null;
                });
    }
}
