package com.example.nasko.whisper.chatroom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.LoadingData;
import com.example.nasko.whisper.models.MessageSeparator;
import com.example.nasko.whisper.models.TypingEvent;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.ArrayRecyclerViewAdapter;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayRecyclerViewAdapter<Object, RecyclerView.ViewHolder> {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_SEPARATOR = 1;
    public static final int TYPE_LOADING = 2;
    public static final int TYPE_TYPING = 3;

    class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.container) FrameLayout container;
        @BindView(R.id.tv_text) TextView tvText;
        @BindView(R.id.tv_time) TextView tvTime;
        @BindView(R.id.message_status_img) ImageView messageStatusImg;

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

        @BindView(R.id.tv_label) TextView tvLabel;

        public TypingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ContactViewModel currentUser;
    private String currentChatId;
    private int messageMaxWidth;

    public MessageAdapter(Context context, ContactViewModel currentUser, String currentChatId, int messageMaxWidth) {
        super(context);
        this.currentUser = currentUser;
        this.currentChatId = currentChatId;
        this.messageMaxWidth = messageMaxWidth;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof MessageViewModel) {
            return TYPE_MESSAGE;
        } else if (item instanceof MessageSeparator) {
            return TYPE_SEPARATOR;
        } else if (item instanceof LoadingData) {
            return TYPE_LOADING;
        } else if (item instanceof TypingEvent) {
            return TYPE_TYPING;
        } else {
            throw new IllegalArgumentException("MessageAdapter does not support type " + item.getClass().getName());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View view = this.getInflater().inflate(R.layout.item_message, parent, false);
                MessageViewHolder holder = new MessageViewHolder(view);
                holder.tvText.setMaxWidth((messageMaxWidth * 4) / 5);
                return holder;
            }
            case 1: {
                View view = this.getInflater().inflate(R.layout.item_message_timestamp_separator, parent, false);
                return new TimeLabelViewHolder(view);
            }
            case 2: {
                View view = this.getInflater().inflate(R.layout.item_message_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            case 3: {
                View view = this.getInflater().inflate(R.layout.item_message_typing, parent, false);
                return new TypingViewHolder(view);
            }
            default:
                throw new UnsupportedOperationException("BaseView type " + viewType + " not supported");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder absHolder, int position) {
        switch (absHolder.getItemViewType()) {
            case 0: {
                MessageViewModel message = (MessageViewModel) getItem(position);
                MessageViewHolder holder = (MessageViewHolder) absHolder;

                holder.tvText.setText(message.getText());

                Date date = message.getCreatedAt();
                String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
                holder.tvTime.setText(dateString);

                boolean isMyMessage = message.getAuthor().getId().equals(currentUser.getId());
                int drawableId = isMyMessage ? R.drawable.bubble_final_right : R.drawable.bubble_final_left;
                int gravity = isMyMessage ? Gravity.END : Gravity.NO_GRAVITY;
                holder.container.setBackgroundResource(drawableId);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.container.getLayoutParams();
                params.gravity = gravity;
                holder.container.setLayoutParams(params);
                if (isMyMessage) {
                    holder.tvText.append(getContext().getString(R.string.m_spaces));
                    holder.messageStatusImg.setVisibility(View.VISIBLE);

                    int resId;
                    switch (message.getStatus()) {
                        case PENDING:
                            resId = R.drawable.clock_sending;
                            break;
                        case SENT:
                            resId = R.drawable.tick_sent;
                            break;
                        default:
                            return;
                    }

                    holder.messageStatusImg.setImageResource(resId);
                } else {
                    holder.tvText.append(getContext().getString(R.string.s_spaces));
                    holder.messageStatusImg.setVisibility(View.GONE);
                }

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
            case 3: {
                TypingEvent typingEvent = (TypingEvent) getItem(position);
                TypingViewHolder holder = (TypingViewHolder) absHolder;

                holder.tvLabel.setText(String.format("%s is typing...", typingEvent.getUsername()));
                break;
            }
            default:
                throw new UnsupportedOperationException(
                        "BaseView type " + absHolder.getItemViewType() + " not supported");
        }
    }
}
