package com.sreesha.android.moviebuzz.DataHandlerClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sreesha on 13-02-2016.
 */
public class MovieDataDBHelper extends SQLiteOpenHelper {

    public static final String TAG = MovieDataDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "movieData.db";
    private static final int DATABASE_VERSION = 25;

    public MovieDataDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = " CREATE TABLE " +
                MovieContract.MovieData.TABLE_MOVIE_DATA
                + "(" + MovieContract.MovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieData.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_PAGE + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_POSTER_PATH + " TEXT,"
                + MovieContract.MovieData.COLUMN_GENRE_IDS + " TEXT,"
                + MovieContract.MovieData.COLUMN_ADULT + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.MovieData.COLUMN_OVERVIEW + " TEXT , "
                + MovieContract.MovieData.COLUMN_RELEASE_DATE + " TEXT , "
                + MovieContract.MovieData.COLUMN_ORIGINAL_TITLE + " TEXT , "
                + MovieContract.MovieData.COLUMN_LANGUAGE + " TEXT , "
                + MovieContract.MovieData.COLUMN_MOVIE_TITLE + " TEXT, "
                + MovieContract.MovieData.COLUMN_BACKDROP_PATH + " TEXT , "
                + MovieContract.MovieData.COLUMN_POPULARITY + " TEXT, "
                + MovieContract.MovieData.COLUMN_VOTE_COUNT + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VIDEO + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_MOVIE_TYPE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_SIMILAR_TO_ID + " INTEGER NOT NULL DEFAULT 0 ,"
                + "UNIQUE (" + MovieContract.MovieData.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_FAVOURITES_TABLE = " CREATE TABLE " +
                MovieContract.UserFavourite.TABLE_FAVOURITE_DATA
                + "(" /*+ MovieContract.UserFavourite._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "*/
                + MovieContract.UserFavourite.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.UserFavourite.COLUMN_MOVIE_TYPE+" INTEGER NOT NULL DEFAULT "+
                MovieContract.UserFavourite.FAVOURITE_MOVIE_TYPE+" , "
                + "PRIMARY KEY (" + MovieContract.UserFavourite.COLUMN_MOVIE_ID +
                " , "+MovieContract.UserFavourite.COLUMN_MOVIE_TYPE+") ON CONFLICT REPLACE"
                + " , FOREIGN KEY(" + MovieContract.UserFavourite.COLUMN_MOVIE_ID + " )"
                + " REFERENCES " + MovieContract.MovieData.TABLE_MOVIE_DATA + "(" + MovieContract.MovieData.COLUMN_MOVIE_ID
                + ")  )";

        final String SQL_CREATE_REVIEWS_TABLE = " CREATE TABLE " +
                MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS
                + "(" + MovieContract.MovieReviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieReviews.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieReviews.COLUMN_PAGE + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieReviews.COLUMN_REVIEW_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieReviews.COLUMN_AUTHOR + " TEXT , "
                + MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT + " TEXT , "
                + MovieContract.MovieReviews.COLUMN_REVIEW_URL + " TEXT , "
                + MovieContract.MovieReviews.COLUMN_TOTAL_PAGES + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + MovieContract.MovieReviews.COLUMN_REVIEW_ID + ")" + ")";

        final String SQL_CREATE_TRAILERS_TABLE = " CREATE TABLE " +
                MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS
                + "(" + MovieContract.MovieTrailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieTrailers.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieTrailers.COLUMN_TRAILER_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieTrailers.COLUMN_ISO_639_1 + " TEXT , "
                + MovieContract.MovieTrailers.COLUMN_TRAILER_KEY + " TEXT , "
                + MovieContract.MovieTrailers.COLUMN_TRAILER_NAME + " TEXT , "
                + MovieContract.MovieTrailers.COLUMN_SITE + " TEXT , "
                + MovieContract.MovieTrailers.COLUMN_SIZE + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieTrailers.COLUMN_TRAILER_TYPE + " TEXT ,"
                + "UNIQUE (" + MovieContract.MovieTrailers.COLUMN_TRAILER_ID + ")" + ")";

        final String SQL_CREATE_CAST_TABLE = " CREATE TABLE " +
                MovieContract.CastData.TABLE_CAST_DATA
                + "(" + MovieContract.CastData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.CastData.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.CastData.COLUMN_CAST_ID + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.CastData.COLUMN_CHARACTER + " TEXT , "
                + MovieContract.CastData.COLUMN_CREDIT_ID + " TEXT , "
                + MovieContract.CastData.COLUMN_NAME + " TEXT , "
                + MovieContract.CastData.COLUMN_PROFILE_PICTURE_PATH + " TEXT , "
                + MovieContract.CastData.COLUMN_UNKNOWN_ID + " INTEGER  , "
                + MovieContract.CastData.COLUMN_ORDER + " INTEGER DEFAULT 0 " + ")";
                /*+"UNIQUE ("+MovieContract.CastData.COLUMN_UNKNOWN_ID+")"*/
        final String SQL_CREATE_CREW_TABLE = " CREATE TABLE " +
                MovieContract.CrewData.TABLE_CREW_DATA
                + "(" + MovieContract.CrewData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.CrewData.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.CrewData.COLUMN_DEPARTMENT + " TEXT , "
                + MovieContract.CrewData.COLUMN_JOB + " TEXT , "
                + MovieContract.CrewData.COLUMN_CREDIT_ID + " TEXT , "
                + MovieContract.CrewData.COLUMN_NAME + " TEXT , "
                + MovieContract.CrewData.COLUMN_PROFILE_PICTURE_PATH + " TEXT , "
                + MovieContract.CrewData.COLUMN_UNKNOWN_ID + " INTEGER "
                /*+"UNIQUE ("+MovieContract.CrewData.COLUMN_UNKNOWN_ID+")"*/ + ")";
        final String SQL_CREATE_WATCHED_MOVIES_TABLE = " CREATE TABLE " +
                MovieContract.WatchedMovieData.TABLE_WATCHED_MOVIE_DATA
                + "(" + MovieContract.MovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieData.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_PAGE + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_POSTER_PATH + " TEXT,"
                + MovieContract.MovieData.COLUMN_GENRE_IDS + " TEXT,"
                + MovieContract.MovieData.COLUMN_ADULT + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.MovieData.COLUMN_OVERVIEW + " TEXT , "
                + MovieContract.MovieData.COLUMN_RELEASE_DATE + " TEXT , "
                + MovieContract.MovieData.COLUMN_ORIGINAL_TITLE + " TEXT , "
                + MovieContract.MovieData.COLUMN_LANGUAGE + " TEXT , "
                + MovieContract.MovieData.COLUMN_MOVIE_TITLE + " TEXT, "
                + MovieContract.MovieData.COLUMN_BACKDROP_PATH + " TEXT , "
                + MovieContract.MovieData.COLUMN_POPULARITY + " TEXT, "
                + MovieContract.MovieData.COLUMN_VOTE_COUNT + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VIDEO + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_MOVIE_TYPE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_SIMILAR_TO_ID + " INTEGER NOT NULL DEFAULT 0 ,"
                + "UNIQUE (" + MovieContract.MovieData.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE)";
        final String SQL_CREATE_TO_WATCH_MOVIES_TABLE = " CREATE TABLE " +
                MovieContract.ToWatchMovieData.TABLE_TO_WATCH_MOVIE_DATA
                + "(" + MovieContract.MovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieData.COLUMN_MOVIE_ID + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_PAGE + " INTEGER NOT NULL DEFAULT 0,"
                + MovieContract.MovieData.COLUMN_POSTER_PATH + " TEXT,"
                + MovieContract.MovieData.COLUMN_GENRE_IDS + " TEXT,"
                + MovieContract.MovieData.COLUMN_ADULT + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.MovieData.COLUMN_OVERVIEW + " TEXT , "
                + MovieContract.MovieData.COLUMN_RELEASE_DATE + " TEXT , "
                + MovieContract.MovieData.COLUMN_ORIGINAL_TITLE + " TEXT , "
                + MovieContract.MovieData.COLUMN_LANGUAGE + " TEXT , "
                + MovieContract.MovieData.COLUMN_MOVIE_TITLE + " TEXT, "
                + MovieContract.MovieData.COLUMN_BACKDROP_PATH + " TEXT , "
                + MovieContract.MovieData.COLUMN_POPULARITY + " TEXT, "
                + MovieContract.MovieData.COLUMN_VOTE_COUNT + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VIDEO + " INTEGER NOT NULL DEFAULT 0 , "
                + MovieContract.MovieData.COLUMN_VOTE_AVERAGE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_MOVIE_TYPE + " INTEGER NOT NULL DEFAULT 0 ,"
                + MovieContract.MovieData.COLUMN_SIMILAR_TO_ID + " INTEGER NOT NULL DEFAULT 0 ,"
                + "UNIQUE (" + MovieContract.MovieData.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE)";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_CAST_TABLE);
        db.execSQL(SQL_CREATE_CREW_TABLE);
       /* db.execSQL(SQL_CREATE_WATCHED_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TO_WATCH_MOVIES_TABLE);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.MovieData.TABLE_MOVIE_DATA);
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.UserFavourite.TABLE_FAVOURITE_DATA);
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS);
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS);
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.CastData.TABLE_CAST_DATA);
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.CrewData.TABLE_CREW_DATA);

        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.ToWatchMovieData.TABLE_TO_WATCH_MOVIE_DATA);
        db.execSQL(" DROP TABLE IF EXISTS " +  MovieContract.WatchedMovieData.TABLE_WATCHED_MOVIE_DATA);

        onCreate(db);
    }
}
