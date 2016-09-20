package com.example.nasko.whisper.models.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Chat implements Parcelable {

    @JsonProperty("_id")
    private String id;
    private Message lastMessage;
    private Contact otherContact;
    @JsonProperty("contacts")
    private List<Contact> participants;

    public Chat() { }

    public Contact getOtherContact() {
        return otherContact;
    }

    public void setOtherContact(Contact otherContact) {
        this.otherContact = otherContact;
    }

    public List<Contact> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Contact> participants) {
        this.participants = participants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        Chat other = (Chat) o;
        return other.getId().equals(this.getId());
    }

    protected Chat(Parcel in) {
        id = in.readString();
        lastMessage = in.readParcelable(Message.class.getClassLoader());
        otherContact = in.readParcelable(Contact.class.getClassLoader());
        participants = in.createTypedArrayList(Contact.CREATOR);
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(lastMessage, flags);
        dest.writeParcelable(otherContact, flags);
        dest.writeTypedList(participants);
    }
}
