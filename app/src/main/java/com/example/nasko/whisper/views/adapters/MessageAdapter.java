package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.User;

import java.util.Date;

public class MessageAdapter extends ArrayRecyclerViewAdapter<Message, MessageAdapter.MessageViewHolder> {

    class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout messageContainer;
        RelativeLayout labelContainer;
        TextView text;
        TextView date;
        TextView label;
        View offsetView;

        MessageViewHolder(View itemView) {
            super(itemView);
            this.labelContainer = (RelativeLayout) itemView.findViewById(R.id.label_container);
            this.label = (TextView) itemView.findViewById(R.id.tv_label);
            this.messageContainer = (LinearLayout) itemView.findViewById(R.id.message_container);
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
        View view = this.getInflater().inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = getItem(position);

        if (message.isDummy()) {
            holder.messageContainer.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
            holder.labelContainer.setVisibility(View.VISIBLE);
            holder.label.setText(message.getLabel());
        } else {
            holder.messageContainer.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
            holder.labelContainer.setVisibility(View.GONE);

            holder.text.setText(message.getText() + "        ");
            Date date = message.getCreatedAt();
            String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
            holder.date.setText(dateString);
            holder.date.invalidate();

            boolean isMyMessage = message.getAuthor().getId().equals(currentUser.getUId());
            int visibility = isMyMessage ? View.VISIBLE : View.GONE;
            int bgDrawable = isMyMessage ? R.drawable.blue_bg : R.drawable.white_bg;
            holder.offsetView.setVisibility(visibility);
            holder.text.setBackgroundResource(bgDrawable);
        }
    }
}
