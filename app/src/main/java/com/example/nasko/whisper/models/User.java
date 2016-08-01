package com.example.nasko.whisper.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String uId;
    private String sessionToken;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(uId);
        dest.writeString(sessionToken);
    }
}
