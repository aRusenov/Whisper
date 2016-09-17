package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.LoadingData;
import com.example.nasko.whisper.models.Message;
import com.example.nasko.whisper.models.MessageSeparator;
import com.example.nasko.whisper.models.User;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayRecyclerViewAdapter<Object, RecyclerView.ViewHolder> {

    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_text) TextView tvText;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.offSet_view) View offsetView;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TimeLabelViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_timestamp) TextView tvTimestamp;

        public TimeLabelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TypingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_timestamp) TextView tvTimestamp;

        public TypingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
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
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof Message) {
            return 0;
        } else if (item instanceof MessageSeparator) {
            return 1;
        } else if (item instanceof LoadingData) {
            return 2;
        } else {
            throw new IllegalArgumentException("MessageAdapter does not support type " + item.getClass().getName());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View view = this.getInflater().inflate(R.layout.item_chat_message, parent, false);
                return new MessageViewHolder(view);
            }
            case 1: {
                View view = this.getInflater().inflate(R.layout.item_message_separator, parent, false);
                return new TimeLabelViewHolder(view);
            }
            case 2: {
                View view = this.getInflater().inflate(R.layout.item_message_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            default:
                throw new UnsupportedOperationException("View type " + viewType + " not supported");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder absHolder, int position) {
        switch (absHolder.getItemViewType()) {
            case 0: {
                Message message = (Message) getItem(position);
                MessageViewHolder holder = (MessageViewHolder) absHolder;

                holder.tvText.setText(message.getText() + "        ");
                Date date = message.getCreatedAt();
                String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
                holder.tvTime.setText(dateString);

                boolean isMyMessage = message.getAuthor().getId().equals(currentUser.getUId());
                int visibility = isMyMessage ? View.VISIBLE : View.GONE;
                int bgDrawable = isMyMessage ? R.drawable.blue_bg : R.drawable.white_bg;
                holder.offsetView.setVisibility(visibility);
                holder.tvText.setBackgroundResource(bgDrawable);
                break;
            }
            case 1: {
                MessageSeparator separator = (MessageSeparator) getItem(position);
                TimeLabelViewHolder holder = (TimeLabelViewHolder) absHolder;

                holder.tvTimestamp.setText(separator.getTimestamp());
                break;
            }
            case 2: {
                // Nothing
                break;
            }
            default:
                throw new UnsupportedOperationException(
                        "View type " + absHolder.getItemViewType() + " not supported");
        }
    }
}
