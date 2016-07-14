package com.sreesha.android.moviebuzz.Networking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Sreesha on 14-05-2016.
 */
public class PersonInstance implements Parcelable {
    private long ID=0;
    private double POPULARITY=0;
    private boolean ADULT=false;
    ArrayList<String> ALSO_KNOWN_AS;
    private String BIOGRAPHY="defaultBiography";
    private String BIRTHDAY="defaultBirthday";
    private String DEATH_DAY="defaultDeathDay";
    private String IMDB_ID="imdbID";
    private String NAME="defaultName";
    private String PLACE_OF_BIRTH="defaultPlaceOfBirth";
    private String PROFILE_PATH="profilePath";

    public PersonInstance(long ID
            , double POPULARITY
            , boolean ADULT
            , String BIOGRAPHY
            , ArrayList<String> ALSO_KNOWN_AS
            , String BIRTHDAY
            , String DEATH_DAY
            , String NAME
            , String IMDB_ID
            , String PLACE_OF_BIRTH
            , String PROFILE_PATH) {
        this.ID = ID;
        this.POPULARITY = POPULARITY;
        this.ADULT = ADULT;
        this.BIOGRAPHY = BIOGRAPHY;
        this.ALSO_KNOWN_AS = ALSO_KNOWN_AS;
        this.BIRTHDAY = BIRTHDAY;
        this.DEATH_DAY = DEATH_DAY;
        this.NAME = NAME;
        this.IMDB_ID = IMDB_ID;
        this.PLACE_OF_BIRTH = PLACE_OF_BIRTH;
        this.PROFILE_PATH = PROFILE_PATH;
    }

    protected PersonInstance(Parcel in) {
        ID = in.readLong();
        POPULARITY = in.readDouble();
        ADULT = in.readByte() != 0;
        ALSO_KNOWN_AS = in.createStringArrayList();
        BIOGRAPHY = in.readString();
        BIRTHDAY = in.readString();
        DEATH_DAY = in.readString();
        IMDB_ID = in.readString();
        NAME = in.readString();
        PLACE_OF_BIRTH = in.readString();
        PROFILE_PATH = in.readString();
    }

    public static final Creator<PersonInstance> CREATOR = new Creator<PersonInstance>() {
        @Override
        public PersonInstance createFromParcel(Parcel in) {
            return new PersonInstance(in);
        }

        @Override
        public PersonInstance[] newArray(int size) {
            return new PersonInstance[size];
        }
    };

    public long getID() {
        return ID;
    }

    public double getPOPULARITY() {
        return POPULARITY;
    }

    public boolean isADULT() {
        return ADULT;
    }

    public ArrayList<String> getALSO_KNOWN_AS() {
        return ALSO_KNOWN_AS;
    }

    public String getBIOGRAPHY() {
        return BIOGRAPHY;
    }

    public String getBIRTHDAY() {
        return BIRTHDAY;
    }

    public String getDEATH_DAY() {
        return DEATH_DAY;
    }

    public String getIMDB_ID() {
        return IMDB_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getPLACE_OF_BIRTH() {
        return PLACE_OF_BIRTH;
    }

    public String getPROFILE_PATH() {
        return PROFILE_PATH;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeDouble(POPULARITY);
        dest.writeByte((byte) (ADULT ? 1 : 0));
        dest.writeStringList(ALSO_KNOWN_AS);
        dest.writeString(BIOGRAPHY);
        dest.writeString(BIRTHDAY);
        dest.writeString(DEATH_DAY);
        dest.writeString(IMDB_ID);
        dest.writeString(NAME);
        dest.writeString(PLACE_OF_BIRTH);
        dest.writeString(PROFILE_PATH);
    }
}
