package com.example.nasko.whisper.chat;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nasko.whisper.Message;
import com.example.nasko.whisper.R;
import com.example.nasko.whisper.User;
import com.example.nasko.whisper.data.listeners.MessagesEventListener;

import java.util.Date;

public class MessageAdapter extends ArrayAdapter<Message> implements MessagesEventListener {

    private User currentUser;
    private String currentChatId;
    private ListView messagesList;

    public MessageAdapter(Context context, int resource, ListView messageList, User currentUser, String currentChatId) {
        super(context, resource);
        this.messagesList = messageList;
        this.currentUser = currentUser;
        this.currentChatId = currentChatId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_layout, parent, false);
        }

        TextView messageView = (TextView) convertView.findViewById(R.id.messageText);
        messageView.setText(message.getText() + "        ");

        TextView messageDate = (TextView) convertView.findViewById(R.id.message_date);
        Date date = message.getDate();
        String dateString = String.format("%02d:%02d", date.getHours(), date.getMinutes());
        messageDate.setText(dateString);

        View offsetView = convertView.findViewById(R.id.offSet_view);
        boolean isMyMessage = message.getFrom().equals(currentUser.getUId());
        int visibility = isMyMessage ? View.VISIBLE : View.GONE;
        int bgDrawable = isMyMessage ? R.drawable.blue_bg : R.drawable.white_bg;
        offsetView.setVisibility(visibility);
        messageView.setBackgroundResource(bgDrawable);

        return convertView;
    }

    @Override
    public void onMessageAdded(Message message) {
        if (! message.getChatId().equals(this.currentChatId)) {
            return;
        }

        int lastVisible = messagesList.getLastVisiblePosition();
        boolean scrollToBottom = lastVisible == this.getCount() - 1;
        this.add(message);

        if (scrollToBottom) {
            messagesList.setSelection(this.getCount() - 1);
        } else if (! currentUser.getUId().equals(message.getFrom())) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this.getContext(), notification);
            r.play();
        }
    }

    @Override
    public void onMessagesLoaded(Message[] messages) {
        int index = messagesList.getFirstVisiblePosition() + messages.length;
        View v = messagesList.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - messagesList.getPaddingTop());

        for (Message msg: messages) {
            this.insert(msg, 0);
        }

        messagesList.setSelectionFromTop(index, top);
    }
}
