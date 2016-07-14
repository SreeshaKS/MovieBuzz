package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 14-05-2016.
 */
public class CrewDataInstance implements Parcelable {
    private String NAME = "defaultName";
    private String CREDIT_ID = "defaultCreditID";
    private String PROFILE_PATH = "defaultProfilePath";
    private long ID = 0;
    private String DEPARTMENT = "defaultDepartment";
    private String JOB = "defaultJob";
    private long MOVIE_ID = 0;

    public long getMOVIE_ID() {
        return MOVIE_ID;
    }


    protected CrewDataInstance(Parcel in) {
        MOVIE_ID = in.readLong();
        NAME = in.readString();
        CREDIT_ID = in.readString();
        PROFILE_PATH = in.readString();
        ID = in.readLong();
        DEPARTMENT = in.readString();
        JOB = in.readString();
    }

    public static final Creator<CrewDataInstance> CREATOR = new Creator<CrewDataInstance>() {
        @Override
        public CrewDataInstance createFromParcel(Parcel in) {
            return new CrewDataInstance(in);
        }

        @Override
        public CrewDataInstance[] newArray(int size) {
            return new CrewDataInstance[size];
        }
    };

    public CrewDataInstance(Long MOVIE_ID, String NAME, String CREDIT_ID, long ID, String PROFILE_PATH, String DEPARTMENT, String JOB) {
        this.MOVIE_ID = MOVIE_ID;
        this.NAME = NAME;
        this.CREDIT_ID = CREDIT_ID;
        this.ID = ID;
        this.PROFILE_PATH = PROFILE_PATH;
        this.DEPARTMENT = DEPARTMENT;
        this.JOB = JOB;
    }

    public String getNAME() {
        return NAME;
    }

    public String getCREDIT_ID() {
        return CREDIT_ID;
    }

    public String getPROFILE_PATH() {
        return PROFILE_PATH;
    }

    public long getID() {
        return ID;
    }

    public String getDEPARTMENT() {
        return DEPARTMENT;
    }

    public String getJOB() {
        return JOB;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(MOVIE_ID);
        dest.writeString(NAME);
        dest.writeString(CREDIT_ID);
        dest.writeString(PROFILE_PATH);
        dest.writeLong(ID);
        dest.writeString(DEPARTMENT);
        dest.writeString(JOB);
    }

    public static CrewDataInstance getCrewDataInstanceFromCursor(Cursor c) {
        return new CrewDataInstance(
                c.getLong(c.getColumnIndex(MovieContract.CrewData.COLUMN_MOVIE_ID))
                , c.getString(c.getColumnIndex(MovieContract.CrewData.COLUMN_NAME))
                , c.getString(c.getColumnIndex(MovieContract.CrewData.COLUMN_CREDIT_ID))
                , c.getLong(c.getColumnIndex(MovieContract.CrewData.COLUMN_UNKNOWN_ID))
                , c.getString(c.getColumnIndex(MovieContract.CrewData.COLUMN_PROFILE_PICTURE_PATH))
                , c.getString(c.getColumnIndex(MovieContract.CrewData.COLUMN_DEPARTMENT))
                , c.getString(c.getColumnIndex(MovieContract.CrewData.COLUMN_JOB))
        );
    }
}
