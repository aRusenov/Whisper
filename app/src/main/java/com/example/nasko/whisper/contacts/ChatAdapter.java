package com.example.nasko.whisper.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nasko.whisper.ArrayRecyclerViewAdapter;
import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.DateProvider;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.data.listeners.ChatsEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayRecyclerViewAdapter<Chat, ChatAdapter.ChatViewHolder> implements ChatsEventListener {

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView contactName;
        TextView msgDate;
        TextView lastMessage;
        CircleImageView profileImg;

        ChatViewHolder(View itemView) {
            super(itemView);
            this.msgDate = (TextView) itemView.findViewById(R.id.message_date);
            this.contactName = (TextView) itemView.findViewById(R.id.contact_name);
            this.lastMessage = (TextView) itemView.findViewById(R.id.last_message);
            this.profileImg = (CircleImageView) itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener listener = getItemClickListener();
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    private DateProvider dateProvider;

    public ChatAdapter(Context context, DateProvider dateProvider) {
        super(context);
        this.dateProvider = dateProvider;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.chat_item_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = this.items.get(position);

        String dateText = getDisplayDate(chat.getLastMessage().getDate());
        holder.msgDate.setText(dateText);
        holder.contactName.setText(chat.getOtherContact().getName());
        holder.lastMessage.setText(chat.getLastMessage().getText());
        Picasso.with(this.getContext())
                .load(chat.getOtherContact().getImageUrl())
                .into(holder.profileImg);
    }

    private String getDisplayDate(Date date) {
        Date today = this.dateProvider.getDate();
        if (today.getDay() == date.getDay()) {
            return String.format("%02d:%02d", date.getHours(), date.getMinutes());
        } else if (today.getDay() - date.getDay() <= 6) {
            DateFormat weekdayFormat = new SimpleDateFormat("E");
            return weekdayFormat.format(date);
        } else {
            DateFormat monthlyFormat = new SimpleDateFormat("W d");
            return monthlyFormat.format(date);
        }
    }

    @Override
    public void onContactAdded(Chat chat) {
        this.add(chat);
    }

    @Override
    public void onContactsLoaded(List<Chat> chats) {
        this.addAll(chats);
    }

    @Override
    public void onContactUpdated(Chat chat) {
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
