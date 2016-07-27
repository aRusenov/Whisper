package com.example.nasko.whisper.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.models.Contact;
import com.example.nasko.whisper.presenters.UserProvider;
import com.example.nasko.whisper.views.listeners.OnItemClickListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactQueryAdapter extends ArrayRecyclerViewAdapter<Contact, ContactQueryAdapter.ContactViewHolder> {

    class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        ImageView inviteIcon;

        ContactViewHolder(View itemView) {
            super(itemView);
            this.image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            this.name = (TextView) itemView.findViewById(R.id.contact_name);
            this.inviteIcon = (ImageView) itemView.findViewById(R.id.invite_icon);

            inviteIcon.setOnClickListener(v -> {
                OnItemClickListener listener = getInvitationIconClickListener();
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    private UserProvider userProvider;
    private OnItemClickListener invitationIconClickListener;

    public ContactQueryAdapter(Context context, UserProvider userProvider) {
        super(context);
        this.userProvider = userProvider;
    }

    public OnItemClickListener getInvitationIconClickListener() {
        return invitationIconClickListener;
    }

    public void setInvitationIconClickListener(OnItemClickListener invitationIconClickListener) {
        this.invitationIconClickListener = invitationIconClickListener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getInflater().inflate(R.layout.contact_query_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = this.getItem(position);
        holder.name.setText(contact.getName());
        Picasso.with(this.getContext())
                .load(contact.getImageUrl())
                .placeholder(R.drawable.blank_pic)
                .into(holder.image);

        boolean isContactUser = userProvider.getCurrentUser().getUId().equals(contact.getId());
        if (isContactUser) {
            holder.inviteIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.inviteIcon.setVisibility(View.VISIBLE);
            if (contact.isFriend()) {
                holder.inviteIcon.setImageResource(R.drawable.sucess_tick);
            } else {
                holder.inviteIcon.setImageResource(R.drawable.letter);
            }
        }
    }

    public void setContactToFriend(Contact contact) {
        int i;
        for (i = 0; i < this.items.size(); i++) {
            if (items.get(i).equals(contact)) {
                break;
            }
        }

        if (i < items.size()) {
            Contact contactRef = this.items.get(i);
            contactRef.setFriend(true);
            this.notifyItemChanged(i);
        }
    }
}
