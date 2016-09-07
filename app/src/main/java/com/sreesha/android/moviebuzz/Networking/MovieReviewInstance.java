package com.sreesha.android.moviebuzz.Networking;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

/**
 * Created by Sreesha on 21-02-2016.
 */
public class MovieReviewInstance implements Parcelable {
    protected long movie_id = 0;
    protected int page = 0;
    protected String review_id = "defaultReview";
    protected String author = "defaultAuthor";
    protected String review_content = "defaultContent";
    protected String review_url = "defaultURL";
    protected String movie_name = "defaultName";

    public int getTotal_pages() {
        return total_pages;
    }

    protected int total_pages = 0;

    public MovieReviewInstance() {
    }

    public MovieReviewInstance(
            long MOVIE_ID
            , int page
            , String review_id
            , String author
            , String review_content
            , String review_url
            , int total_pages) {
        this.movie_id = MOVIE_ID;
        this.page = page;
        this.review_id = review_id;
        this.author = author;
        this.review_content = review_content;
        this.review_url = review_url;
        this.total_pages = total_pages;
    }

    public MovieReviewInstance(
            long MOVIE_ID
            , int page
            , String review_id
            , String author
            , String review_content
            , String review_url
            , int total_pages
            , String movie_name) {
        this.movie_id = MOVIE_ID;
        this.page = page;
        this.review_id = review_id;
        this.author = author;
        this.review_content = review_content;
        this.review_url = review_url;
        this.total_pages = total_pages;
        this.movie_name = movie_name;
    }

    public MovieReviewInstance(Parcel in) {
        movie_id = in.readLong();
        page = in.readInt();
        review_id = in.readString();
        author = in.readString();
        review_content = in.readString();
        review_url = in.readString();
        total_pages = in.readInt();
        movie_name=in.readString();
    }

    public String getReview_url() {
        return review_url;
    }

    public String getReview_content() {
        return review_content;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview_id() {
        return review_id;
    }

    public int getPage() {
        return page;
    }

    public long getMovie_id() {
        return movie_id;
    }
    public String getMovie_name() {
        return movie_name;
    }

    public static final Creator<MovieReviewInstance> CREATOR = new Creator<MovieReviewInstance>() {
        @Override
        public MovieReviewInstance createFromParcel(Parcel in) {
            return new MovieReviewInstance(in);
        }

        @Override
        public MovieReviewInstance[] newArray(int size) {
            return new MovieReviewInstance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movie_id);
        dest.writeInt(page);
        dest.writeString(review_id);
        dest.writeString(author);
        dest.writeString(review_content);
        dest.writeString(review_url);
        dest.writeInt(total_pages);
        dest.writeString(movie_name);
    }

    public static MovieReviewInstance getMovieReviewInstanceFromCursor(Cursor movieReviewCursor) {

        return new MovieReviewInstance(
                movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_MOVIE_ID))
                , movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_PAGE))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_ID))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_AUTHOR))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT))
                , movieReviewCursor.getString(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_REVIEW_URL))
                , movieReviewCursor.getInt(movieReviewCursor.getColumnIndex(MovieContract.MovieReviews.COLUMN_MOVIE_ID))
        );
    }

    public static MovieReviewInstance getMovieReviewInstanceFromDataSnapShot(DataSnapshot snap) {

        return new MovieReviewInstance(
                Long.parseLong(snap.child("movie_id").getValue().toString())
                , -1
                , snap.child("review_id").getValue().toString()
                , snap.child("author").getValue().toString()
                , snap.child("review_content").getValue().toString()
                , snap.child("review_url").getValue().toString()
                , 0
                ,snap.child("movie_name").getValue().toString()
        );
    }
}
