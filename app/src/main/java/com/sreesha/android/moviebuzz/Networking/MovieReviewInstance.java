package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 21-02-2016.
 */
public class MovieReviewInstance implements Parcelable {
    private long MOVIE_ID = 0;
    private int PAGE = 0;
    private String REVIEW_ID = "defaultReview";
    private String AUTHOR = "defaultAuthor";
    private String REVIEW_CONTENT = "defaultContent";
    private String REVIEW_URL = "defaultURL";

    public int getTOTAL_PAGES() {
        return TOTAL_PAGES;
    }

    private int TOTAL_PAGES = 0;

    public MovieReviewInstance(
            long MOVIE_ID
            , int PAGE
            , String REVIEW_ID
            , String AUTHOR
            , String REVIEW_CONTENT
            , String REVIEW_URL
            , int TOTAL_PAGES) {
        this.MOVIE_ID = MOVIE_ID;
        this.PAGE = PAGE;
        this.REVIEW_ID = REVIEW_ID;
        this.AUTHOR = AUTHOR;
        this.REVIEW_CONTENT = REVIEW_CONTENT;
        this.REVIEW_URL = REVIEW_URL;
        this.TOTAL_PAGES = TOTAL_PAGES;
    }

    public MovieReviewInstance(Parcel in) {
        MOVIE_ID = in.readLong();
        PAGE = in.readInt();
        REVIEW_ID = in.readString();
        AUTHOR = in.readString();
        REVIEW_CONTENT = in.readString();
        REVIEW_URL = in.readString();
        TOTAL_PAGES = in.readInt();
    }

    public String getREVIEW_URL() {
        return REVIEW_URL;
    }

    public String getREVIEW_CONTENT() {
        return REVIEW_CONTENT;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public String getREVIEW_ID() {
        return REVIEW_ID;
    }

    public int getPAGE() {
        return PAGE;
    }

    public long getMOVIE_ID() {
        return MOVIE_ID;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(MOVIE_ID);
        dest.writeInt(PAGE);
        dest.writeString(REVIEW_ID);
        dest.writeString(AUTHOR);
        dest.writeString(REVIEW_CONTENT);
        dest.writeString(REVIEW_URL);
        dest.writeInt(TOTAL_PAGES);
    }

    public static MovieReviewInstance getMovieReviewInstanceFromCursor(Cursor movieReviewCursor) {

        return new MovieReviewInstance(
                movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_MOVIE_ID))
                , movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_PAGE))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_ID))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_AUTHOR))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT))
                , movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_MOVIE_ID))
        );
    }
}
