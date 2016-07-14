package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 21-02-2016.
 */
public class MovieTrailerInstance implements Parcelable {
    private long MOVIE_ID = 0;
    private String TRAILER_ID = "defaultTrailer";
    private String ISO_639_1 = "iso_639_1";
    private String TRAILER_KEY = "defaultTrailerKey";
    private String TRAILER_NAME = "trailerName";
    private String SITE = "defaultSite";
    private int SIZE = 0;
    private String TRAILER_TYPE = "defaultTrailerType";

    public MovieTrailerInstance(long MOVIE_ID
            , String TRAILER_ID
            , String TRAILER_KEY
            , String ISO_639_1
            , String TRAILER_NAME
            , String SITE
            , int SIZE
            , String TRAILER_TYPE) {
        this.MOVIE_ID = MOVIE_ID;
        this.TRAILER_ID = TRAILER_ID;
        this.TRAILER_KEY = TRAILER_KEY;
        this.ISO_639_1 = ISO_639_1;
        this.TRAILER_NAME = TRAILER_NAME;
        this.SITE = SITE;
        this.SIZE = SIZE;
        this.TRAILER_TYPE = TRAILER_TYPE;
    }

    protected MovieTrailerInstance(Parcel in) {
        MOVIE_ID = in.readLong();
        TRAILER_ID = in.readString();
        ISO_639_1 = in.readString();
        TRAILER_KEY = in.readString();
        TRAILER_NAME = in.readString();
        SITE = in.readString();
        SIZE = in.readInt();
        TRAILER_TYPE = in.readString();
    }

    public static final Creator<MovieTrailerInstance> CREATOR = new Creator<MovieTrailerInstance>() {
        @Override
        public MovieTrailerInstance createFromParcel(Parcel in) {
            return new MovieTrailerInstance(in);
        }

        @Override
        public MovieTrailerInstance[] newArray(int size) {
            return new MovieTrailerInstance[size];
        }
    };

    public String getTRAILER_TYPE() {
        return TRAILER_TYPE;
    }

    public int getSIZE() {
        return SIZE;
    }

    public String getSITE() {
        return SITE;
    }

    public String getTRAILER_NAME() {
        return TRAILER_NAME;
    }

    public String getTRAILER_KEY() {
        return TRAILER_KEY;
    }

    public String getISO_639_1() {
        return ISO_639_1;
    }

    public String getTRAILER_ID() {
        return TRAILER_ID;
    }

    public long getMOVIE_ID() {
        return MOVIE_ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(MOVIE_ID);
        dest.writeString(TRAILER_ID);
        dest.writeString(ISO_639_1);
        dest.writeString(TRAILER_KEY);
        dest.writeString(TRAILER_NAME);
        dest.writeString(SITE);
        dest.writeInt(SIZE);
        dest.writeString(TRAILER_TYPE);
    }

    public static MovieTrailerInstance getMovieTrailerInstanceFromCursor(Cursor movieTrailerCursor) {
        return (new MovieTrailerInstance(
                movieTrailerCursor.getInt(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_MOVIE_ID))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_TRAILER_ID))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_TRAILER_KEY))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_ISO_639_1))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_TRAILER_NAME))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_SITE))
                , movieTrailerCursor.getInt(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_SIZE))
                , movieTrailerCursor.getString(movieTrailerCursor.getColumnIndex(MovieContract.MovieTrailers.COLUMN_TRAILER_TYPE))
        ));
    }
}
