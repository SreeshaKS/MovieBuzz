package com.sreesha.android.moviebuzz.Networking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 30-07-2016.
 */
public class User implements Parcelable{
    private String username;
    private String email;
    private String uid;
    private String photoURL;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String uid, String photoURL) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.photoURL = photoURL;
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        uid = in.readString();
        photoURL = in.readString();
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

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeString(photoURL);
    }
}
