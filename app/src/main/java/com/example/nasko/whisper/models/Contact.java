package com.example.nasko.whisper.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.nasko.whisper.managers.ImageUrlResolver;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact implements Parcelable {
    private String username;
    private String name;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("isFriend")
    private boolean isFriend;
    private boolean isUser;
    @JsonProperty("online")
    private boolean isOnline;

    public Contact() { }

    public Contact(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getImageUrl() {
        return ImageUrlResolver.getFullUrl(image);
    }

    public void setImageUrl(String url) {
        if (image != null) {
            image.setUrl(url);
        }
    }

    @Override
    public boolean equals(Object o) {
        Contact other = (Contact) o;
        return this.id.equals(other.getId());
    }

    protected Contact(Parcel in) {
        username = in.readString();
        name = in.readString();
        id = in.readString();
        image = in.readParcelable(Image.class.getClassLoader());
        isFriend = in.readByte() != 0;
        isOnline = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeParcelable(image, flags);
        dest.writeByte((byte) (isFriend ? 1 : 0));
        dest.writeByte((byte) (isOnline? 1 : 0));
    }
}
