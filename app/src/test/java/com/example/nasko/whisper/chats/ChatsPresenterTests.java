package com.example.nasko.whisper.chats;

import com.example.nasko.whisper.RxSchedulersOverrideRule;
import com.example.nasko.whisper.chats.interactors.ChatsInteractor;
import com.example.nasko.whisper.chats.interactors.ContactsStateInteractor;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatsPresenterTests {

    private static final MessageViewModel SAMPLE_MESSAGE = new MessageViewModel("msg_id", "some_ext", new Date(), null);
    private static final ChatViewModel SAMPLE_CHAT = new ChatViewModel("some_id", SAMPLE_MESSAGE);

    private static final PublishSubject<ChatViewModel> FAKE_NEW_CHAT_SOURCE = PublishSubject.create();
    private static final PublishSubject<MessageViewModel> FAKE_NEW_MESSAGE_SOURCE = PublishSubject.create();
    private static final PublishSubject<List<ChatViewModel>> FAKE_CHATS_LOADED_SOURCE = PublishSubject.create();

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

        when(chatsInteractor.onChatsLoaded()).thenReturn(FAKE_CHATS_LOADED_SOURCE);
        when(chatsInteractor.onNewChat()).thenReturn(FAKE_NEW_CHAT_SOURCE);
        when(chatsInteractor.onChatNewMessage()).thenReturn(FAKE_NEW_MESSAGE_SOURCE);

        when(connectionInteractor.onUserOnline()).thenReturn(Observable.never());
        when(connectionInteractor.onUserOffline()).thenReturn(Observable.never());

        presenter = new ChatsPresenter(view, viewCoordinator,
                chatsInteractor, connectionInteractor);
    }

    @Test
    public void clickOnChat_ShouldCallViewNavigator() {
        ChatViewModel someChat = new ChatViewModel("some_id", null);
        presenter.onChatClicked(someChat);
        verify(viewCoordinator).onChatItemClicked(eq(someChat));
    }

    @Test
    public void onInit_shouldCall_InteractorsInit() {
        presenter.init();
        verify(chatsInteractor).init();
        verify(connectionInteractor).init();
    }

    @Test
    public void when_OnNewMessage_emits_shouldCallView_UpdateChatLastMessage() {
        presenter.init();
        verify(view, never()).updateChatLastMessage(any(), any());

        FAKE_NEW_MESSAGE_SOURCE.onNext(SAMPLE_MESSAGE);
        verify(view).updateChatLastMessage(eq(SAMPLE_MESSAGE.getChatId()), eq(SAMPLE_MESSAGE));
    }

    @Test
    public void when_OnNewChat_emits_should_callView_AddChat() {
        presenter.init();
        verify(view, never()).addChat(any());

        FAKE_NEW_CHAT_SOURCE.onNext(SAMPLE_CHAT);
        verify(view).addChat(eq(SAMPLE_CHAT));
    }

    @Test
    public void when_OnChatsLoaded_emits_shouldClearOldChatsAndLoadNew() {
        presenter.init();

        List<ChatViewModel> chats = Arrays.asList(new ChatViewModel(null, null), new ChatViewModel(null, null));
        FAKE_CHATS_LOADED_SOURCE.onNext(chats);

        verify(view).clearChats();
        verify(view).loadChats(eq(chats));
    }

    @Test
    public void onDestroy_shouldCall_InteractorsDestroy() {
        presenter.init();
        presenter.destroy();
        verify(chatsInteractor).destroy();
        verify(connectionInteractor).destroy();
    }
}
