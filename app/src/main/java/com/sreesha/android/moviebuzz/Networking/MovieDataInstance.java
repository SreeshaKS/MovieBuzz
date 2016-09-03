package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 28-01-2016.
 */
public class MovieDataInstance implements Parcelable {
    private int PAGE_NO = 0;
    private int TOTAL_RESULTS = 0;
    private int TOTAL_PAGES = 0;
    private String POSTER_PATH = "defaultPosterPath";
    private Boolean CERTIFICATION = false;
    private String OVERVIEW = "defaultOverView";
    private String RELEASE_DATE = "defaultDate";
    private int[] GENRE_ID_ARRAY = {0};

    public String getGENRE_ID_JSON_ARRAY_STRING() {
        return GENRE_ID_JSON_ARRAY_STRING;
    }

    private String GENRE_ID_JSON_ARRAY_STRING = "{\"key\":\"value\"}";
    private long ID = 0;
    private String ORIGINAL_TITLE = "defaultOriginalTitle";
    private String ORIGINAL_LANGUAGE = "defaultOriginalLanguage";
    private String TITLE = "defaultTitle";
    private String BACKDROP_PATH = "defaultBackDopPath";
    private Double POPULARITY = 0.0000000;
    private long VOTE_COUNT = 0;
    private Boolean VIDEO_BOOL = false;
    private Double VOTE_AVERAGE = 0.0000;


    public MovieDataInstance() {

    }

    public MovieDataInstance(
            int PAGE_NO
            , int TOTAL_RESULTS
            , int TOTAL_PAGES
            , String POSTER_PATH
            , Boolean CERTIFICATION
            , String OVERVIEW
            , String RELEASE_DATE
            , int[] GENRE_ID_ARRAY
            , long ID
            , String ORIGINAL_TITLE
            , String ORIGINAL_LANGUAGE
            , String TITLE
            , String BACKDROP_PATH
            , Double POPULARITY
            , long VOTE_COUNT
            , Boolean VIDEO_BOOL
            , Double VOTE_AVERAGE
            , String GENRE_ID_JSON_ARRAY_STRING
    ) {
        this.PAGE_NO = PAGE_NO;
        this.TOTAL_RESULTS = TOTAL_RESULTS;
        this.TOTAL_PAGES = TOTAL_PAGES;
        this.POSTER_PATH = POSTER_PATH;
        this.CERTIFICATION = CERTIFICATION;
        this.OVERVIEW = OVERVIEW;
        this.RELEASE_DATE = RELEASE_DATE;
        this.GENRE_ID_ARRAY = GENRE_ID_ARRAY;
        this.ID = ID;
        this.ORIGINAL_TITLE = ORIGINAL_TITLE;
        this.ORIGINAL_LANGUAGE = ORIGINAL_LANGUAGE;
        this.TITLE = TITLE;
        this.BACKDROP_PATH = BACKDROP_PATH;
        this.POPULARITY = POPULARITY;
        this.VOTE_COUNT = VOTE_COUNT;
        this.VIDEO_BOOL = VIDEO_BOOL;
        this.VOTE_AVERAGE = VOTE_AVERAGE;
        this.GENRE_ID_JSON_ARRAY_STRING = GENRE_ID_JSON_ARRAY_STRING;
        Log.e("VoteDebug", "" + VOTE_AVERAGE);
    }

    protected MovieDataInstance(Parcel in) {
        PAGE_NO = in.readInt();
        TOTAL_RESULTS = in.readInt();
        TOTAL_PAGES = in.readInt();
        POSTER_PATH = in.readString();
        CERTIFICATION = convertIntToBool(in.readInt());
        VIDEO_BOOL = convertIntToBool(in.readInt());
        OVERVIEW = in.readString();
        RELEASE_DATE = in.readString();
        GENRE_ID_ARRAY = in.createIntArray();
        ID = in.readLong();
        ORIGINAL_TITLE = in.readString();
        ORIGINAL_LANGUAGE = in.readString();
        TITLE = in.readString();
        BACKDROP_PATH = in.readString();
        VOTE_COUNT = in.readLong();
        VOTE_AVERAGE = in.readDouble();
        POPULARITY = in.readDouble();
        GENRE_ID_JSON_ARRAY_STRING = in.readString();
    }

    public static final Creator<MovieDataInstance> CREATOR = new Creator<MovieDataInstance>() {
        @Override
        public MovieDataInstance createFromParcel(Parcel in) {
            return new MovieDataInstance(in);
        }

        @Override
        public MovieDataInstance[] newArray(int size) {
            return new MovieDataInstance[size];
        }
    };

    public int getPageNumber() {
        return PAGE_NO;
    }

    public int getTotalResult() {
        return TOTAL_RESULTS;
    }

    public int getTotalPages() {
        return TOTAL_PAGES;
    }

    public String getPOSTER_PATH() {
        return POSTER_PATH;
    }

    public Boolean getCertification() {
        return CERTIFICATION;
    }

    public String getOverView() {
        return OVERVIEW;
    }

    public String getReleaseDate() {
        return RELEASE_DATE;
    }

    public int[] getGenreIDArray() {
        return GENRE_ID_ARRAY;
    }

    public long getMovieID() {
        return ID;
    }

    public String getOriginalTitle() {
        return ORIGINAL_TITLE;
    }

    public String getOriginalLanguage() {
        return ORIGINAL_LANGUAGE;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getBackDropPath() {
        return BACKDROP_PATH;
    }

    public Double getPopularity() {
        return POPULARITY;
    }

    public long getVoteCount() {
        return VOTE_COUNT;
    }

    public Boolean getVideoBool() {
        return VIDEO_BOOL;
    }

    public Double getAverageVoting() {
        return VOTE_AVERAGE;
    }

    public void setPageNumber(int val) {
        PAGE_NO = val;
    }

    public void setTotalResults(int val) {
        TOTAL_RESULTS = val;
    }

    public void setTotalPages(int val) {
        TOTAL_PAGES = val;
    }

    public void setPOSTER_PATH(String val) {
        POSTER_PATH = val;
    }

    public void setCertification(Boolean val) {
        CERTIFICATION = val;
    }

    public void setOverView(String val) {
        OVERVIEW = val;
    }

    public void setReleaseDate(String val) {
        RELEASE_DATE = val;
    }

    public void setGenreIDArray(int[] val) {
        GENRE_ID_ARRAY = val;
    }

    public void setMovieID(long val) {
        ID = val;
    }

    public void getOriginalTitle(String val) {
        ORIGINAL_TITLE = val;
    }

    public void setOriginalLanguage(String val) {
        ORIGINAL_LANGUAGE = val;
    }

    public void setTitle(String val) {
        TITLE = val;
    }

    public void setBackDropPath(String val) {
        BACKDROP_PATH = val;
    }

    public void setPopularity(Double val) {
        POPULARITY = val;
    }

    public void setVoteCount(long val) {
        VOTE_COUNT = val;
    }

    public void setVideoBool(Boolean val) {
        VIDEO_BOOL = val;
    }

    public void setAverageVoting(Double val) {
        VOTE_AVERAGE = val;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(PAGE_NO);
        dest.writeInt(TOTAL_RESULTS);
        dest.writeInt(TOTAL_PAGES);
        dest.writeString(POSTER_PATH);
        dest.writeInt(convertBoolToInt(CERTIFICATION));
        dest.writeInt(convertBoolToInt(VIDEO_BOOL));
        dest.writeString(OVERVIEW);
        dest.writeString(RELEASE_DATE);
        dest.writeIntArray(GENRE_ID_ARRAY);
        dest.writeLong(ID);
        dest.writeString(ORIGINAL_TITLE);
        dest.writeString(ORIGINAL_LANGUAGE);
        dest.writeString(TITLE);
        dest.writeString(BACKDROP_PATH);
        dest.writeLong(VOTE_COUNT);
        dest.writeDouble(VOTE_AVERAGE);
        dest.writeDouble(POPULARITY);
        dest.writeString(GENRE_ID_JSON_ARRAY_STRING);
    }

    private int convertBoolToInt(Boolean val) {
        int i = 0;
        if (val) i = 1;
        return i;
    }

    private Boolean convertIntToBool(int val) {
        boolean i = true;
        if (val == 0) i = false;
        return i;
    }

    public static MovieDataInstance getMovieDataInstanceFromCursor(Cursor cursor) {
        return new MovieDataInstance(
                cursor.getInt(
                        cursor.getColumnIndex(MovieContract.MovieData.COLUMN_PAGE)
                )
                , 0
                , 0
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_POSTER_PATH))
                , (cursor.getInt(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_ADULT)) == 1)
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_OVERVIEW))
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_RELEASE_DATE))
                , new int[]{0, 0}/*Genre Array*/
                , cursor.getLong(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_MOVIE_ID))
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_ORIGINAL_TITLE))
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_LANGUAGE))
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_MOVIE_TITLE))
                , cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_BACKDROP_PATH))
                , Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_POPULARITY)))
                , cursor.getInt(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_VOTE_COUNT))
                , cursor.getInt(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_VIDEO)) == 1
                , (double) cursor.getInt(
                cursor.getColumnIndex(MovieContract.MovieData.COLUMN_VOTE_AVERAGE))
                , cursor.getString(cursor.getColumnIndex(MovieContract.MovieData.COLUMN_GENRE_IDS))
        );
    }
}
