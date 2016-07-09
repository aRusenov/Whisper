package com.example.nasko.whisper.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nasko.whisper.Chat;
import com.example.nasko.whisper.DateProvider;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.data.listeners.ChatsEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayAdapter<Chat> implements ChatsEventListener {

    private DateProvider dateProvider;

    public ChatAdapter(Context context, int resource, DateProvider dateProvider) {
        super(context, resource);
        this.dateProvider = dateProvider;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_layout, parent, false);
        }

        String dateText = getDisplayDate(chat.getLastMessage().getDate());
        TextView tvDate = (TextView) convertView.findViewById(R.id.message_date);
        tvDate.setText(dateText);

        TextView tvContactName = (TextView) convertView.findViewById(R.id.contact_name);
        tvContactName.setText(chat.getOtherContact().getName());

        TextView tvLastMessage = (TextView) convertView.findViewById(R.id.last_message);
        tvLastMessage.setText(chat.getLastMessage().getText());

        CircleImageView imgProfile = (CircleImageView) convertView.findViewById(R.id.profile_image);
        Picasso.with(this.getContext()).load(chat.getOtherContact().getImageUrl()).into(imgProfile);

        return convertView;
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
    public void onContactsLoaded(Chat[] chats) {
        this.addAll(chats);
    }

    @Override
    public void onContactUpdated(Chat chat) {
        this.remove(chat);
        this.insert(chat, 0);
    }

    @Override
    public void onContactAdded(Chat chat) {
        this.add(chat);
    }
}
