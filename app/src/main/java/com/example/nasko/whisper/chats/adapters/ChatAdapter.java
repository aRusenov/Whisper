package com.example.nasko.whisper.chats.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.WhisperApplication;
import com.example.nasko.whisper.data.local.UserProvider;
import com.example.nasko.whisper.models.view.ChatViewModel;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.models.view.MessageViewModel;
import com.example.nasko.whisper.utils.ArrayRecyclerViewAdapter;
import com.example.nasko.whisper.utils.DateFormatter;
import com.example.nasko.whisper.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayRecyclerViewAdapter<ChatViewModel, ChatAdapter.ChatViewHolder> {

    class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_contact_name) TextView contactName;
        @BindView(R.id.message_date) TextView msgDate;
        @BindView(R.id.last_message) TextView lastMessage;
        @BindView(R.id.profile_image) CircleImageView profileImg;
        @BindView(R.id.status_image) ImageView statusImg;
        @BindView(R.id.tv_msg_prefix) TextView tvMsgPrefix;

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                OnItemClickListener listener = getItemClickListener();
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    private DateFormatter dateFormatter;
    private UserProvider userProvider;

    public ChatAdapter(Context context, DateFormatter dateFormatter, UserProvider userProvider) {
        super(context);
        this.dateFormatter = dateFormatter;
        this.userProvider = userProvider;
    }

    public ChatAdapter(Context context, DateFormatter dateFormatter) {
        this(context, dateFormatter, WhisperApplication.instance().getUserProvider());
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatViewModel chat = this.items.get(position);
        ContactViewModel displayContact = chat.getDisplayContact();

        String dateText = dateFormatter.getStringFormat(getContext(), chat.getLastMessage().getCreatedAt());
        holder.msgDate.setText(dateText);
        String name = displayContact.getName();
        if (name == null) {
            name = displayContact.getUsername();
        }

        holder.contactName.setText(name);
        holder.lastMessage.setText(chat.getLastMessage().getText());
        int prefixVisibility = isMessageAuthor(chat.getLastMessage()) ? View.VISIBLE : View.GONE;
        holder.tvMsgPrefix.setVisibility(prefixVisibility);

        int statusRes = displayContact.isOnline() ? R.drawable.circle_green : R.drawable.circle_gray;
        holder.statusImg.setImageResource(statusRes);

        Picasso picasso = Picasso.with(getContext());
        picasso.load(displayContact.getImage().getUrl())
                .placeholder(R.drawable.blank_pic)
                .into(holder.profileImg);
    }

    private boolean isMessageAuthor(MessageViewModel message) {
        if (userProvider != null && userProvider.getCurrentUser() != null) {
            String userId = userProvider.getCurrentUser().getUId();
            if (message.getAuthor().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    public int findIndexById(String id) {
        for (int i = 0; i < items.size(); i++) {
            ChatViewModel chat = items.get(i);
            if (chat.getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public void moveToTop(int position) {
        // Remove item from old position
        ChatViewModel oldChat = this.items.remove(position);

        // Insert item at start
        this.items.add(0, oldChat);
        this.notifyItemMoved(position, 0);
    }
}
