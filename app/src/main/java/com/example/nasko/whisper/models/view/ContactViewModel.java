package com.example.nasko.whisper.models.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.nasko.whisper.models.dto.Image;

public class ContactViewModel implements Parcelable {

    private String id;
    private String username;
    private String name;
    private Image image;
    private boolean online;

    public ContactViewModel(String id, String username, String name, Image image, boolean online) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.image = image;
        this.online = online;
    }

    protected ContactViewModel(Parcel in) {
        id = in.readString();
        username = in.readString();
        name = in.readString();
        image = in.readParcelable(Image.class.getClassLoader());
        online = in.readInt() == 1;
    }

    public static final Creator<ContactViewModel> CREATOR = new Creator<ContactViewModel>() {
        @Override
        public ContactViewModel createFromParcel(Parcel in) {
            return new ContactViewModel(in);
        }

        @Override
        public ContactViewModel[] newArray(int size) {
            return new ContactViewModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeParcelable(image, flags);
        dest.writeInt(online ? 1 : 0);
    }
}
