package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 14-05-2016.
 */
public class CastDataInstance implements Parcelable {
    private String NAME = "defaultName";
    private String CREDIT_ID = "defaultCreditID";
    private String PROFILE_PATH = "defaultProfilePath";
    private int ORDER = 0;
    private long ID = 0;

    public long getMOVIE_ID() {
        return MOVIE_ID;
    }

    private long MOVIE_ID = 0;
    private long CAST_ID = 0;
    private String CHARACTER = "defaultCharacter";

    protected CastDataInstance(Parcel in) {
        MOVIE_ID = in.readLong();
        NAME = in.readString();
        CREDIT_ID = in.readString();
        PROFILE_PATH = in.readString();
        ORDER = in.readInt();
        ID = in.readLong();
        CAST_ID = in.readLong();
        CHARACTER = in.readString();
    }

    public static final Creator<CastDataInstance> CREATOR = new Creator<CastDataInstance>() {
        @Override
        public CastDataInstance createFromParcel(Parcel in) {
            return new CastDataInstance(in);
        }

        @Override
        public CastDataInstance[] newArray(int size) {
            return new CastDataInstance[size];
        }
    };

    public CastDataInstance(Long MOVIE_ID, String NAME, String CREDIT_ID, String PROFILE_PATH, int ORDER, long ID, long CAST_ID, String CHARACTER) {
        this.MOVIE_ID = MOVIE_ID;
        this.NAME = NAME;
        this.CREDIT_ID = CREDIT_ID;
        this.PROFILE_PATH = PROFILE_PATH;
        this.ORDER = ORDER;
        this.ID = ID;
        this.CAST_ID = CAST_ID;
        this.CHARACTER = CHARACTER;
    }

    public long getID() {
        return ID;
    }

    public long getCAST_ID() {
        return CAST_ID;
    }

    public String getCHARACTER() {
        return CHARACTER;
    }

    public String getCREDIT_ID() {
        return CREDIT_ID;
    }

    public String getPROFILE_PATH() {
        return PROFILE_PATH;
    }

    public int getORDER() {
        return ORDER;
    }


    public String getNAME() {
        return NAME;
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
        dest.writeInt(ORDER);
        dest.writeLong(ID);
        dest.writeLong(CAST_ID);
        dest.writeString(CHARACTER);
    }

    public static CastDataInstance getCastDataInstanceFromCursor(Cursor c) {
        return new CastDataInstance(
                c.getLong(c.getColumnIndex(MovieContract.CastData.COLUMN_MOVIE_ID))
                ,c.getString(c.getColumnIndex(MovieContract.CastData.COLUMN_NAME))
                ,c.getString(c.getColumnIndex(MovieContract.CastData.COLUMN_CREDIT_ID))
                ,c.getString(c.getColumnIndex(MovieContract.CastData.COLUMN_PROFILE_PICTURE_PATH))
                ,c.getInt(c.getColumnIndex(MovieContract.CastData.COLUMN_ORDER))
                ,c.getLong(c.getColumnIndex(MovieContract.CastData.COLUMN_UNKNOWN_ID))
                ,c.getLong(c.getColumnIndex(MovieContract.CastData.COLUMN_CAST_ID))
                ,c.getString(c.getColumnIndex(MovieContract.CastData.COLUMN_CHARACTER))
        );
    }
}
