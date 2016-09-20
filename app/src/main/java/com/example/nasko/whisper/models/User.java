package com.example.nasko.whisper.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.nasko.whisper.models.dto.Image;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Parcelable {
    private String username;
    @JsonProperty("uId")
    private String uId;
    private String name;
    @JsonProperty("token")
    private String sessionToken;
    private Image image;

    public User() {}

    public User(String username, String uId, String sessionToken) {
        this.username = username;
        this.uId = uId;
        this.sessionToken = sessionToken;
    }

    protected User(Parcel in) {
        username = in.readString();
        uId = in.readString();
        sessionToken = in.readString();
        name = in.readString();
        image = in.readParcelable(Image.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public String getUId() {
        return uId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(uId);
        dest.writeString(sessionToken);
        dest.writeString(name);
        dest.writeParcelable(image, flags);
    }
}
