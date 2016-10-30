package com.example.nasko.whisper.models.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.nasko.whisper.models.MessageStatus;

import java.util.Date;

public class MessageViewModel implements Parcelable {

    private long identifier;
    private String chatId;
    private MessageStatus status;
    private String text;
    private Date createdAt;
    private int sequentialId;
    private ContactViewModel author;

    public MessageViewModel(String chatId, String text, Date createdAt, ContactViewModel author) {
        this(chatId, text, createdAt, author, 0, MessageStatus.SENT);
    }

    public MessageViewModel(String chatId, String text, Date createdAt,
                            ContactViewModel author, long identifier, MessageStatus status) {
        this.identifier = identifier;
        this.chatId = chatId;
        this.status = status;
        this.text = text;
        this.createdAt = createdAt;
        this.author = author;
    }

    // FIXME: How is date not put here but gets restored afterwards?
    protected MessageViewModel(Parcel in) {
        identifier = in.readLong();
        chatId = in.readString();
        text = in.readString();
        author = in.readParcelable(ContactViewModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(identifier);
        dest.writeString(chatId);
        dest.writeString(text);
        dest.writeParcelable(author, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageViewModel> CREATOR = new Creator<MessageViewModel>() {
        @Override
        public MessageViewModel createFromParcel(Parcel in) {
            return new MessageViewModel(in);
        }

        @Override
        public MessageViewModel[] newArray(int size) {
            return new MessageViewModel[size];
        }
    };

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ContactViewModel getAuthor() {
        return author;
    }

    public void setAuthor(ContactViewModel author) {
        this.author = author;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setUId(long uId) {
        this.identifier = uId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getSequentialId() {
        return sequentialId;
    }

    public void setSequentialId(int sequentialId) {
        this.sequentialId = sequentialId;
    }
}
