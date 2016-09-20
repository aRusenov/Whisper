package com.example.nasko.whisper.models.view;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatViewModel implements Parcelable {

    private String id;
    private MessageViewModel lastMessage;
    private ContactViewModel displayContact;

    public ChatViewModel(String id, MessageViewModel lastMessage) {
        this.id = id;
        this.lastMessage = lastMessage;
    }

    protected ChatViewModel(Parcel in) {
        id = in.readString();
        lastMessage = in.readParcelable(MessageViewModel.class.getClassLoader());
        displayContact = in.readParcelable(ContactViewModel.class.getClassLoader());
    }

    public static final Creator<ChatViewModel> CREATOR = new Creator<ChatViewModel>() {
        @Override
        public ChatViewModel createFromParcel(Parcel in) {
            return new ChatViewModel(in);
        }

        @Override
        public ChatViewModel[] newArray(int size) {
            return new ChatViewModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageViewModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageViewModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ContactViewModel getDisplayContact() {
        return displayContact;
    }

    public void setDisplayContact(ContactViewModel displayContact) {
        this.displayContact = displayContact;
    }

    @Override
    public boolean equals(Object o) {
        ChatViewModel other = (ChatViewModel) o;
        return this.id.equals(other.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(lastMessage, flags);
        dest.writeParcelable(displayContact, flags);
    }
}
