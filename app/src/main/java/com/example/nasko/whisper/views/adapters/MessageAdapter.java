package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.User;

import java.util.Date;

public class MessageAdapter extends ArrayRecyclerViewAdapter<Message, MessageAdapter.MessageViewHolder> {

    class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView date;
        View offsetView;

        MessageViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.message_text);
            this.date = (TextView) itemView.findViewById(R.id.message_date);
            this.offsetView = itemView.findViewById(R.id.offSet_view);
        }
    }

    private User currentUser;
    private String currentChatId;

    public MessageAdapter(Context context, User currentUser, String currentChatId) {
        super(context);
        this.currentUser = currentUser;
        this.currentChatId = currentChatId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.chat_message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = getItem(position);

        holder.text.setText(message.getText() + "        ");
        Date date = message.getDate();
        String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
        holder.date.setText(dateString);
        holder.date.invalidate();

        boolean isMyMessage = message.getFrom().equals(currentUser.getUId());
        int visibility = isMyMessage ? View.VISIBLE : View.GONE;
        int bgDrawable = isMyMessage ? R.drawable.blue_bg : R.drawable.white_bg;
        holder.offsetView.setVisibility(visibility);
        holder.text.setBackgroundResource(bgDrawable);
    }

//    @Override
//    public void onMessageAdded(Message message) {
//        if (! message.getChatId().equals(this.currentChatId)) {
//            return;
//        }
//
//        LinearLayoutManager layoutManager = ((LinearLayoutManager)messagesList.getLayoutManager());
//
//        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
//        boolean scrollToBottom = lastVisible == this.getItemCount() - 1;
//        this.add(message);
//
//        if (scrollToBottom) {
//            messagesList.getLayoutManager().scrollToPosition(this.getItemCount() - 1);
//        } else if (! currentUser.getUId().equals(message.getFrom())) {
//            this.playNewMessageSound();
//        }
//    }
//
//    @Override
//    public void onMessagesLoaded(List<Message> messages) {
//        // Insert messages at top
//        this.items.addAll(0, messages);
//        this.notifyItemRangeInserted(0, messages.size());
//
//        // Maintain scroll position
//        int index = messagesList.getFirstVisiblePosition() + messages.size();
//        View topmostView = messagesList.getChildAt(0);
//        int top = (topmostView == null) ? 0 : (topmostView.getTop() - messagesList.getPaddingTop());
//        messagesList.setSelectionFromTop(index, top);
//    }
}
