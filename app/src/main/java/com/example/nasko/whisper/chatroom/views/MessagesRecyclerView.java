package com.example.nasko.whisper.chatroom.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.example.nasko.whisper.chatroom.adapters.MessageAdapter;
import com.example.nasko.whisper.models.LoadingData;
import com.example.nasko.whisper.models.MessageStatus;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.view.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessagesRecyclerView extends RecyclerView {

    private LinearLayoutManager layoutManager;
    private MessageAdapter adapter;

    public MessagesRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(MessageAdapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        super.setLayoutManager(layoutManager);
    }

    public void addMessageAtEnd(MessageViewModel message) {
        int insertPosition = adapter.size();
        if (adapter.last() instanceof TypingEvent) {
            insertPosition--;
        }

        boolean scroll = isLastItemVisible();
        removeTypingEvent();
        adapter.add(insertPosition, message);
        if (scroll) {
            scrollToPosition(adapter.getItemCount() - 1);
        } else {
            // TODO: Add new message indicator
        }
    }

    public void updateMessageStatus(long msgIdentifier, MessageStatus status) {
        for (int i = 0; i < adapter.size(); i++) {
            Object item = adapter.getItem(i);
            if (item instanceof MessageViewModel) {
                MessageViewModel msg = (MessageViewModel)item;
                if (msg.getIdentifier() == msgIdentifier) {
                    msg.setStatus(status);
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
        }
    }

    public void addMessagesAtStart(List<Object> messages) {
        removeLoadingIfPresent();
        if (messages.isEmpty()) {
            return;
        }

        // Insert items at top
        adapter.addAllAt(0, messages);

        // Maintain scroll position
        int index = layoutManager.findFirstVisibleItemPosition() + messages.size();
        scrollToPosition(index);
    }

    public void addTypingEvent(TypingEvent typingEvent) {
        boolean scrollToBottom = isLastItemVisible();
        adapter.add(typingEvent);
        if (scrollToBottom) {
            scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void removeTypingEvent() {
        if (adapter.last() instanceof TypingEvent) {
            boolean scrollToBottom = isLastItemVisible();
            adapter.removeAt(adapter.size() - 1);
            if (scrollToBottom) {
                scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    public void addLoading() {
        adapter.add(0, new LoadingData());
    }

    public void removeLoadingIfPresent() {
        if (adapter.size() > 0 && adapter.getItem(0) instanceof LoadingData) {
            adapter.removeAt(0);
        }
    }

    public ArrayList<MessageViewModel> getMessages() {
        ArrayList<MessageViewModel> savedMessages = new ArrayList<>();
        for (Object item : adapter) {
            if (item instanceof MessageViewModel) {
                savedMessages.add((MessageViewModel) item);
            }
        }

        return savedMessages;
    }

    private boolean isLastItemVisible() {
        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisible == adapter.getItemCount() - 1;
    }
}
