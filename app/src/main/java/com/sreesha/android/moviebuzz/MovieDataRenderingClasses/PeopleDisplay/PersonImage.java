package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 09-09-2016.
 */
public class PersonImage implements Parcelable{
    private String iso_639_1;
    private String imagePath;
    private int height;
    private int width;
    private double vote_average;
    private int voteCount;
    private double aspectRatio;

    public PersonImage( String iso_639_1
            , String imagePath, int height, int width, double vote_average, int voteCount
            , double aspectRatio) {
        this.iso_639_1 = iso_639_1;
        this.imagePath = imagePath;
        this.height = height;
        this.width = width;
        this.vote_average = vote_average;
        this.voteCount = voteCount;
        this.aspectRatio = aspectRatio;
    }

    protected PersonImage(Parcel in) {
        iso_639_1 = in.readString();
        imagePath = in.readString();
        height = in.readInt();
        width = in.readInt();
        vote_average = in.readDouble();
        voteCount = in.readInt();
        aspectRatio = in.readDouble();
    }

    public static final Creator<PersonImage> CREATOR = new Creator<PersonImage>() {
        @Override
        public PersonImage createFromParcel(Parcel in) {
            return new PersonImage(in);
        }

        @Override
        public PersonImage[] newArray(int size) {
            return new PersonImage[size];
        }
    };

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

    public double getAspectRatio() {
        return aspectRatio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(iso_639_1);
        parcel.writeString(imagePath);
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeDouble(vote_average);
        parcel.writeInt(voteCount);
        parcel.writeDouble(aspectRatio);
    }
}
