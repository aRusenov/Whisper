package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Chat;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.views.listeners.OnItemClickListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayRecyclerViewAdapter<Chat, ChatAdapter.ChatViewHolder> {

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView contactName;
        TextView msgDate;
        TextView lastMessage;
        CircleImageView profileImg;
        ImageView statusImg;

        ChatViewHolder(View itemView) {
            super(itemView);
            this.msgDate = (TextView) itemView.findViewById(R.id.message_date);
            this.contactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            this.lastMessage = (TextView) itemView.findViewById(R.id.last_message);
            this.profileImg = (CircleImageView) itemView.findViewById(R.id.profile_image);
            this.statusImg = (ImageView) itemView.findViewById(R.id.status_image);

            itemView.setOnClickListener(v -> {
                OnItemClickListener listener1 = getItemClickListener();
                if (listener1 != null) {
                    listener1.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    private DateFormatter dateFormatter;

    public ChatAdapter(Context context, DateFormatter dateFormatter) {
        super(context);
        this.dateFormatter = dateFormatter;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = this.items.get(position);

        String dateText = dateFormatter.getStringFormat(getContext(), chat.getLastMessage().getCreatedAt());
        holder.msgDate.setText(dateText);
        String name = chat.getOtherContact().getName();
        if (name == null) {
            name = chat.getOtherContact().getUsername();
        }

        holder.contactName.setText(name);
        holder.lastMessage.setText(chat.getLastMessage().getText());

        int statusRes = chat.getOtherContact().isOnline() ? R.drawable.circle_green : R.drawable.circle_gray;
        holder.statusImg.setImageResource(statusRes);

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.load(chat.getOtherContact().getImageUrl())
                .placeholder(R.drawable.blank_pic)
                .into(holder.profileImg);
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
