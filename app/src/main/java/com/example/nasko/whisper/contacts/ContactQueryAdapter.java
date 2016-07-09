package com.example.nasko.whisper.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nasko.whisper.Contact;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactQueryAdapter extends ArrayAdapter<Contact> {

    private User currentUser;

    public ContactQueryAdapter(Context context, int resource, User currentUser) {
        super(context, resource);
        this.currentUser = currentUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_query_item, parent, false);
        }

        CircleImageView imgProfile = (CircleImageView) convertView.findViewById(R.id.profile_image);
        Picasso.with(this.getContext()).load(contact.getImageUrl()).into(imgProfile);

        TextView tvName = (TextView) convertView.findViewById(R.id.contact_name);
        tvName.setText(contact.getName());

        if (!contact.isFriend() && !currentUser.getUId().equals(contact.getId())) {
            ImageView inviteIcon = (ImageView) convertView.findViewById(R.id.invite_icon);
            inviteIcon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
