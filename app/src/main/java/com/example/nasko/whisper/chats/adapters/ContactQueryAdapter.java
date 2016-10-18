package com.example.nasko.whisper.chats.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.User;
import com.example.nasko.whisper.models.view.ContactViewModel;
import com.example.nasko.whisper.utils.ArrayRecyclerViewAdapter;
import com.example.nasko.whisper.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactQueryAdapter extends ArrayRecyclerViewAdapter<ContactViewModel, ContactQueryAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image) CircleImageView image;
        @BindView(R.id.tv_contact_name) TextView name;
        @BindView(R.id.invite_icon) ImageView inviteIcon;

        ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            inviteIcon.setOnClickListener(v -> {
                OnItemClickListener listener = getInvitationIconClickListener();
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    private OnItemClickListener invitationIconClickListener;
    private User currentUser;

    public ContactQueryAdapter(Context context, User user) {
        super(context);
        this.currentUser = user;
    }

    OnItemClickListener getInvitationIconClickListener() {
        return invitationIconClickListener;
    }

    public void setInvitationIconClickListener(OnItemClickListener invitationIconClickListener) {
        this.invitationIconClickListener = invitationIconClickListener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.item_contact_query, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactViewModel contact = this.getItem(position);
        boolean isContactUser = currentUser.getUId().equals(contact.getId());

        holder.name.setText(contact.getUsername());
        if (isContactUser) {
            holder.name.append(getContext().getString(R.string.prefix_contact_item_user));
        }

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.load(contact.getImage().getUrl())
                .placeholder(R.drawable.blank_pic)
                .into(holder.image);

        if (isContactUser) {
            holder.inviteIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.inviteIcon.setVisibility(View.VISIBLE);
            if (contact.isFriend()) {
                holder.inviteIcon.setImageResource(R.drawable.sucess_tick);
                holder.inviteIcon.setClickable(false);
            } else {
                holder.inviteIcon.setImageResource(R.drawable.letter);
                holder.inviteIcon.setClickable(true);
            }
        }
    }

    public void setContactToFriend(ContactViewModel contact) {
        // Find contact index in array
        int i;
        for (i = 0; i < this.items.size(); i++) {
            if (items.get(i).equals(contact)) {
                break;
            }
        }

        // Update if present
        if (i != -1 && i < items.size()) {
            ContactViewModel contactRef = this.items.get(i);
            contactRef.setFriend(true);
            this.notifyItemChanged(i);
        }
    }
}
