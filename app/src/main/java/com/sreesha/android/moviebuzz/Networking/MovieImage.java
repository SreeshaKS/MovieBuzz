package com.sreesha.android.moviebuzz.Networking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 16-05-2016.
 */
public class MovieImage implements Parcelable {
    private boolean isBackDrop;
    private String iso_639_1;
    private String imagePath;
    private int height;
    private int width;
    private double vote_average;
    private int voteCount;

    public MovieImage(boolean isBackDrop, String iso_639_1, String imagePath, int height, int width, double vote_average, int voteCount) {
        this.isBackDrop = isBackDrop;
        this.iso_639_1 = iso_639_1;
        this.imagePath = imagePath;
        this.height = height;
        this.width = width;
        this.vote_average = vote_average;
        this.voteCount = voteCount;
    }

    protected MovieImage(Parcel in) {
        isBackDrop = in.readByte() != 0;
        iso_639_1 = in.readString();
        imagePath = in.readString();
        height = in.readInt();
        width = in.readInt();
        vote_average = in.readDouble();
        voteCount = in.readInt();
    }

    public static final Creator<MovieImage> CREATOR = new Creator<MovieImage>() {
        @Override
        public MovieImage createFromParcel(Parcel in) {
            return new MovieImage(in);
        }

        @Override
        public MovieImage[] newArray(int size) {
            return new MovieImage[size];
        }
    };

    public boolean isBackDrop() {
        return isBackDrop;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getHeight() {
        return height;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVoteCount() {
        return voteCount;
    }


    public int getWidth() {
        return width;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isBackDrop ? 1 : 0));
        dest.writeString(iso_639_1);
        dest.writeString(imagePath);
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeDouble(vote_average);
        dest.writeInt(voteCount);
    }
}
