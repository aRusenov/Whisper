package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.RxSchedulersOverrideRule;
import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractor;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ChatViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatsPresenterTests {

    private static final String CHAT_ID = "some_id";
    private static final User USER = new User("pesho", "user_id", "session_token");

    @Rule
    public RxSchedulersOverrideRule schedulersOverrideRule = new RxSchedulersOverrideRule();

    @Mock ChatsContract.View view;
    @Mock ViewCoordinator viewCoordinator;
    @Mock ChatsInteractor chatsInteractor;
    @Mock ContactsStateInteractor connectionInteractor;

    private ChatsContract.Presenter presenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(chatsInteractor.onChatsLoaded()).thenReturn(Observable.never());
        when(chatsInteractor.onNewChat()).thenReturn(Observable.never());
        when(chatsInteractor.onChatNewMessage()).thenReturn(Observable.never());

        when(connectionInteractor.onUserOnline()).thenReturn(Observable.never());
        when(connectionInteractor.onUserOffline()).thenReturn(Observable.never());

        presenter = new ChatsPresenter(view, viewCoordinator,
                chatsInteractor, connectionInteractor);
    }

    @Test
    public void clickOnChat_ShouldCallViewNavigator() {
        ChatViewModel someChat = new ChatViewModel(CHAT_ID, null);
        presenter.onChatClicked(someChat);
        verify(viewCoordinator).onChatItemClicked(someChat);
    }
}
