package com.example.nasko.whisper.models.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Message implements Parcelable {

    @JsonProperty("_id")
    private String id;
    private Date createdAt;
    private String text;
    private String chatId;
    @JsonProperty("createdBy")
    private Contact author;
    private int seq;

    private boolean isDummy;
    private String label;

    protected Message(Parcel in) {
        id = in.readString();
        text = in.readString();
        chatId = in.readString();
        author = in.readParcelable(Contact.class.getClassLoader());
        seq = in.readInt();
        isDummy = in.readByte() != 0;
        label = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Contact getAuthor() {
        return author;
    }

    public Message() { }

    public Message(String text, String chatId) {
        this.text = text;
        this.chatId = chatId;
    }

    private Message(String label) {
        this.isDummy = true;
        this.label = label;
    }

    public static Message createDummy(String label) {
        return new Message(label);
    }

    public boolean isDummy() {
        return isDummy;
    }

    public String getLabel() {
        return label;
    }

    public int getSeq() {
        return seq;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(chatId);
        dest.writeParcelable(author, flags);
        dest.writeInt(seq);
        dest.writeByte((byte) (isDummy ? 1 : 0));
        dest.writeString(label);
    }
}
