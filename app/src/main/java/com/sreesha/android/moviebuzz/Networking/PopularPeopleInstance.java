package com.sreesha.android.moviebuzz.Networking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sreesha on 08-07-2016.
 */
public class PopularPeopleInstance implements Parcelable {
    private long ID = 0;
    private double POPULARITY = 0;
    ArrayList<MovieDataInstance> mKnownForMovieDataArrayList;
    private String NAME = "defaultName";
    private String PROFILE_PATH = "profilePath";
    private long PAGE_NUMBER = 0;

    public PopularPeopleInstance(
            long ID
            , double POPULARITY
            , ArrayList<MovieDataInstance> mKnownForMovieDataArrayList
            , String PROFILE_PATH
            , String NAME
            , long PAGE_NUMBER) {
        this.ID = ID;
        this.POPULARITY = POPULARITY;
        this.mKnownForMovieDataArrayList = mKnownForMovieDataArrayList;
        this.PROFILE_PATH = PROFILE_PATH;
        this.NAME = NAME;
        this.PAGE_NUMBER = PAGE_NUMBER;
    }

    protected PopularPeopleInstance(Parcel in) {
        ID = in.readLong();
        POPULARITY = in.readDouble();
        mKnownForMovieDataArrayList = in.createTypedArrayList(MovieDataInstance.CREATOR);
        NAME = in.readString();
        PROFILE_PATH = in.readString();
        PAGE_NUMBER=in.readLong();
    }

    public static final Creator<PopularPeopleInstance> CREATOR = new Creator<PopularPeopleInstance>() {
        @Override
        public PopularPeopleInstance createFromParcel(Parcel in) {
            return new PopularPeopleInstance(in);
        }

        @Override
        public PopularPeopleInstance[] newArray(int size) {
            return new PopularPeopleInstance[size];
        }
    };

    public ArrayList<MovieDataInstance> getmKnownForMovieDataArrayList() {
        return mKnownForMovieDataArrayList;
    }

    public String getPROFILE_PATH() {
        return PROFILE_PATH;
    }

    public String getNAME() {
        return NAME;
    }

    public double getPOPULARITY() {
        return POPULARITY;
    }

    public long getID() {
        return ID;
    }

    public long getPAGE_NUMBER() {
        return PAGE_NUMBER;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeDouble(POPULARITY);
        dest.writeTypedList(mKnownForMovieDataArrayList);
        dest.writeString(NAME);
        dest.writeString(PROFILE_PATH);
        dest.writeLong(PAGE_NUMBER);
    }
}
