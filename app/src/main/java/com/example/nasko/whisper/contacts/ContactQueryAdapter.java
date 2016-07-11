package com.example.nasko.whisper.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.ArrayRecyclerViewAdapter;
import com.example.nasko.whisper.Contact;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
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

    private User currentUser;

    public ContactQueryAdapter(Context context, User currentUser) {
        super(context);
        this.currentUser = currentUser;
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
                .into(holder.image);

        boolean isContactUser = currentUser.getUId().equals(contact.getId());
        if (!contact.isFriend() && !isContactUser) {
            holder.inviteIcon.setVisibility(View.VISIBLE);
        }
    }
}
