package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.utils.DateProvider;
import com.example.nasko.whisper.views.listeners.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayRecyclerViewAdapter<Chat, ChatAdapter.ChatViewHolder> {

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView contactName;
        TextView msgDate;
        TextView lastMessage;
        CircleImageView profileImg;

        ChatViewHolder(View itemView) {
            super(itemView);
            this.msgDate = (TextView) itemView.findViewById(R.id.message_date);
            this.contactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            this.lastMessage = (TextView) itemView.findViewById(R.id.last_message);
            this.profileImg = (CircleImageView) itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(v -> {
                OnItemClickListener listener1 = getItemClickListener();
                if (listener1 != null) {
                    listener1.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    private static final DateFormat WEEKDAY_FORMAT = new SimpleDateFormat("E");
    private static final DateFormat MONTHLY_FORMAT = new SimpleDateFormat("W d");

    private DateProvider dateProvider;

    public ChatAdapter(Context context, DateProvider dateProvider) {
        super(context);
        this.dateProvider = dateProvider;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = this.items.get(position);

        String dateText = getDisplayDate(chat.getLastMessage().getCreatedAt());
        holder.msgDate.setText(dateText);
        String name = chat.getOtherContact().getName();
        if (name == null) {
            name = chat.getOtherContact().getUsername();
        }

        holder.contactName.setText(name);
        holder.lastMessage.setText(chat.getLastMessage().getText());

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.load(chat.getOtherContact().getImageUrl())
                .placeholder(R.drawable.blank_pic)
                .into(holder.profileImg);
    }

    private String getDisplayDate(Date date) {
        Date today = this.dateProvider.getDate();
        if (today.getDay() == date.getDay()) {
            return String.format("%02d:%02d", date.getHours(), date.getMinutes());
        } else if (today.getDay() - date.getDay() <= 6) {
            return WEEKDAY_FORMAT.format(date);
        } else {
            return MONTHLY_FORMAT.format(date);
        }
    }

    public void update(Chat chat) {
        // Remove item from old position
        int oldPosition = this.items.indexOf(chat);
        Chat oldChat = this.items.remove(oldPosition);
        oldChat.setLastMessage(chat.getLastMessage());

        // Insert item at start
        this.items.add(0, oldChat);
        this.notifyItemChanged(oldPosition);
        this.notifyItemMoved(oldPosition, 0);
    }
}
