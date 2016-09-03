package com.sreesha.android.moviebuzz.DataHandlerClasses;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.ParseException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;

/**
 * Created by Sreesha on 09-02-2016.
 */
public class MovieDataProvider extends ContentProvider {

    private static final String TAG = MovieDataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int MOVIE_DATA = 1995;
    private static final int MOVIE_DATA_ID = 1996;

    private static final int MOVIE_TRAILER_DATA = 1997;
    private static final int MOVIE_TRAILER_DATA_ID = 1998;

    private static final int MOVIE_REVIEWS_DATA = 1999;
    private static final int MOVIE_REVIEWS_DATA_ID = 2000;

    private static final int MOVIE_FAVOURITES_DATA_ID = 2001;
    private static final int MOVIE_FAVOURITES_INSERT_WITH_ID = 2002;

    private static final int MOVIE_FAVOURITES_MOVIE_DATA_JOINED = 2003;

    private static final int MOVIE_CAST_DATA = 2004;
    private static final int MOVIE_CAST_DATA_ID = 2005;

    private static final int MOVIE_CREW_DATA = 2006;
    private static final int MOVIE_CREW_DATA_ID = 2007;

    MovieDataDBHelper mMovieDataDBHelper;
    private static final SQLiteQueryBuilder sFavouritesFromMovieIDQueryBuilder;

    static {
        sFavouritesFromMovieIDQueryBuilder = new SQLiteQueryBuilder();
        sFavouritesFromMovieIDQueryBuilder.setTables(
                MovieContract.MovieData.TABLE_MOVIE_DATA
                        + " INNER JOIN "
                        + MovieContract.UserFavourite.TABLE_FAVOURITE_DATA
                        + " ON " + MovieContract.UserFavourite.TABLE_FAVOURITE_DATA
                        + "." + MovieContract.UserFavourite.COLUMN_MOVIE_ID
                        + " = " + MovieContract.MovieData.TABLE_MOVIE_DATA
                        + "." + MovieContract.MovieData.COLUMN_MOVIE_ID
        );
    }

    private Cursor getFavouredMovies(Uri uri, String[] projection
            , String selection, String[] selectionArgs
            , String sortOrder) {
        return sFavouritesFromMovieIDQueryBuilder.query(
                mMovieDataDBHelper.getReadableDatabase()
                , null
                , selection
                , selectionArgs
                , null
                , null
                , sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIE_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_TRAILERS, MOVIE_TRAILER_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_TRAILERS + "/#", MOVIE_TRAILER_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEWS, MOVIE_REVIEWS_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEWS + "/#", MOVIE_REVIEWS_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_CAST_DATA, MOVIE_CAST_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_CAST_DATA + "/#", MOVIE_CAST_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_CREW_DATA, MOVIE_CREW_DATA);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_CREW_DATA + "/#", MOVIE_CREW_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVOURITES, MOVIE_FAVOURITES_INSERT_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVOURITES + "/#", MOVIE_FAVOURITES_DATA_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVOURITES
                + "/"
                + MovieContract.PATH_FAVOURITES_MOVIE_DATA, MOVIE_FAVOURITES_MOVIE_DATA_JOINED);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mMovieDataDBHelper = new MovieDataDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("CastCrewDebug", "Query Function Called");
        Log.d("Watched", "Calling Query Function For Watched");
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DATA: {
                Log.e("SimilarMDDebug", "Query Function Called");
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieData.TABLE_MOVIE_DATA
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
            }
            case MOVIE_TRAILER_DATA: {
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , null);
            }
            case MOVIE_TRAILER_DATA_ID: {
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS
                        , projection
                        , MovieContract.MovieTrailers.COLUMN_MOVIE_ID + " = ?"
                        , new String[]{String.valueOf(ContentUris.parseId(uri))}
                        , null
                        , null
                        , sortOrder
                );
            }
            case MOVIE_REVIEWS_DATA: {
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , null);
            }
            case MOVIE_REVIEWS_DATA_ID: {
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS
                        , projection
                        , MovieContract.MovieReviews.COLUMN_MOVIE_ID + " = ?"
                        , new String[]{String.valueOf(ContentUris.parseId(uri))}
                        , null
                        , null
                        , sortOrder
                );
            }
            case MOVIE_DATA_ID: {
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.MovieData.TABLE_MOVIE_DATA
                        , projection
                        , MovieContract.MovieData.COLUMN_MOVIE_ID + " = ?"
                        , new String[]{String.valueOf(ContentUris.parseId(uri))}
                        , null
                        , null
                        , sortOrder);
            }
            case MOVIE_FAVOURITES_DATA_ID: {
                Log.d("Watched", "Calling Query Function For Watched");

                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.UserFavourite.TABLE_FAVOURITE_DATA
                        , projection
                        , MovieContract.MovieData.COLUMN_MOVIE_ID + " = ?"
                        , new String[]{String.valueOf(ContentUris.parseId(uri))}
                        , null
                        , null
                        , null);
            }
            case MOVIE_FAVOURITES_MOVIE_DATA_JOINED: {
                return getFavouredMovies(uri, projection, selection, selectionArgs, sortOrder);
            }
            case MOVIE_CAST_DATA:

                Log.e("CastCrewDebug", "Cast Database Queried");
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.CastData.TABLE_CAST_DATA
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , null);

            case MOVIE_CREW_DATA:
                return mMovieDataDBHelper.getReadableDatabase().query(
                        MovieContract.CrewData.TABLE_CREW_DATA
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , null);

            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DATA: {
                return MovieContract.MovieData.CONTENT_DIR_TYPE;
            }
            case MOVIE_DATA_ID: {
                return MovieContract.MovieData.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDataDBHelper.getWritableDatabase();
        Uri returnUri = null;
        long _id = -1;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DATA: {

                _id = db.insert(MovieContract.MovieData.TABLE_MOVIE_DATA, null, values);

                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.MovieData.buildMovieDataUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case MOVIE_TRAILER_DATA: {
                _id = db.insert(MovieContract.MovieTrailers.TABLE_MOVIE_TRAILERS, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.MovieData.buildMovieDataUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case MOVIE_REVIEWS_DATA: {
                _id = db.insert(MovieContract.MovieReviews.TABLE_MOVIE_REVIEWS, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.MovieData.buildMovieDataUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case MOVIE_FAVOURITES_INSERT_WITH_ID: {
                _id = db.insert(MovieContract.UserFavourite.TABLE_FAVOURITE_DATA, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.UserFavourite.FAVOURITES_CONTENT_URI;
                    Log.e("DatabaseDebug", "inserted \t" + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case MOVIE_CAST_DATA:
                _id = db.insert(MovieContract.CastData.TABLE_CAST_DATA, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.CastData.MOVIE_CAST_CONTENT_URI;
                    Log.e("DatabaseDebug", "inserted \t" + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            case MOVIE_CREW_DATA:
                _id = db.insert(MovieContract.CrewData.TABLE_CREW_DATA, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.CrewData.MOVIE_CREW_CONTENT_URI;
                    Log.e("DatabaseDebug", "inserted \t" + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            case MOVIE_CAST_DATA_ID:
                _id = db.insert(MovieContract.CastData.TABLE_CAST_DATA, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.CastData.MOVIE_CAST_CONTENT_URI;
                    Log.e("DatabaseDebug", "inserted \t" + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            case MOVIE_CREW_DATA_ID:
                _id = db.insert(MovieContract.CrewData.TABLE_CREW_DATA, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.CrewData.MOVIE_CREW_CONTENT_URI;
                    Log.e("DatabaseDebug", "inserted \t" + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Object m = new Object();
        m.getClass().getName();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDataDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case MOVIE_DATA:
                numDeleted = db.delete(
                        MovieContract.MovieData.TABLE_MOVIE_DATA, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieData.TABLE_MOVIE_DATA + "'");
                break;
            case MOVIE_DATA_ID:
                numDeleted = db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA,
                        MovieContract.MovieData._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieData.TABLE_MOVIE_DATA + "'");

                break;
            case MOVIE_FAVOURITES_DATA_ID:
                numDeleted = db.delete(MovieContract.UserFavourite.TABLE_FAVOURITE_DATA,
                        MovieContract.UserFavourite.COLUMN_MOVIE_ID + " = ?"
                                + " AND " + MovieContract.UserFavourite.COLUMN_MOVIE_TYPE + " =?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))
                                , String.valueOf(selectionArgs[0])}
                );
                Log.e("DatabaseDebug", "Deleted number =\t" + numDeleted);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDataDBHelper.getWritableDatabase();
        int numUpdated = 0;
        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }
        switch (sUriMatcher.match(uri)) {
            case MOVIE_DATA: {
                numUpdated = db.update(MovieContract.MovieData.TABLE_MOVIE_DATA,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_DATA_ID: {
                numUpdated = db.update(MovieContract.MovieData.TABLE_MOVIE_DATA,
                        values,
                        MovieContract.MovieData._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMovieDataDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numInserted = -1;
        switch (match) {
            case MOVIE_DATA:
                // allows for multiple transactions
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(MovieContract.MovieData.TABLE_MOVIE_DATA,
                                    null, value);
                            Log.e("SimilarMDDebug", "Insert Function Called : " + _id);
                        } catch (SQLiteConstraintException e) {
                            e.printStackTrace();
                        }
                        if (_id != -1) {
                            numInserted++;
                            Log.e("InsertDebug", "Inserting : " + numInserted);
                        } else if (value.getAsInteger(MovieContract.MovieData.COLUMN_MOVIE_TYPE) == MovieContract.MovieData.SIMILAR_MOVIE_TYPE) {

                            ContentValues newValue = new ContentValues();
                            newValue.put(MovieContract.MovieData.COLUMN_MOVIE_ID, value.getAsString(MovieContract.MovieData.COLUMN_MOVIE_ID));
                            newValue.put(MovieContract.MovieData.COLUMN_PAGE, value.getAsInteger(MovieContract.MovieData.COLUMN_PAGE));
                            newValue.put(MovieContract.MovieData.COLUMN_POSTER_PATH, value.getAsString(MovieContract.MovieData.COLUMN_POSTER_PATH));
                            newValue.put(MovieContract.MovieData.COLUMN_ADULT, value.getAsInteger(MovieContract.MovieData.COLUMN_ADULT));
                            newValue.put(MovieContract.MovieData.COLUMN_OVERVIEW, value.getAsString(MovieContract.MovieData.COLUMN_OVERVIEW));
                            newValue.put(MovieContract.MovieData.COLUMN_RELEASE_DATE, value.getAsString(MovieContract.MovieData.COLUMN_RELEASE_DATE));
                            newValue.put(MovieContract.MovieData.COLUMN_ORIGINAL_TITLE, value.getAsString(MovieContract.MovieData.COLUMN_ORIGINAL_TITLE));
                            newValue.put(MovieContract.MovieData.COLUMN_LANGUAGE, value.getAsString(MovieContract.MovieData.COLUMN_LANGUAGE));
                            newValue.put(MovieContract.MovieData.COLUMN_MOVIE_TITLE, value.getAsString(MovieContract.MovieData.COLUMN_MOVIE_TITLE));
                            newValue.put(MovieContract.MovieData.COLUMN_BACKDROP_PATH, value.getAsString(MovieContract.MovieData.COLUMN_BACKDROP_PATH));
                            newValue.put(MovieContract.MovieData.COLUMN_POPULARITY, value.getAsDouble(MovieContract.MovieData.COLUMN_POPULARITY));
                            newValue.put(MovieContract.MovieData.COLUMN_VOTE_COUNT, value.getAsLong(MovieContract.MovieData.COLUMN_VOTE_COUNT));
                            newValue.put(MovieContract.MovieData.COLUMN_VIDEO, value.getAsInteger(MovieContract.MovieData.COLUMN_VIDEO));
                            //values.put(MovieContract.MovieData.COLUMN_MOVIE_ID,value.getAsString(MovieContract.MovieData.COLUMN_MOVIE_ID));
                            newValue.put(MovieContract.MovieData.COLUMN_VOTE_AVERAGE, value.getAsDouble(MovieContract.MovieData.COLUMN_VOTE_AVERAGE));
                            newValue.put(MovieContract.MovieData.COLUMN_SIMILAR_TO_ID
                                    ,
                                    value.getAsLong(MovieContract.MovieData.COLUMN_SIMILAR_TO_ID)
                            );
                            newValue.put(MovieContract.MovieData.COLUMN_GENRE_IDS, value.getAsString(MovieContract.MovieData.COLUMN_GENRE_IDS));
                            _id = db.update(MovieContract.MovieData.TABLE_MOVIE_DATA
                                    , newValue
                                    , MovieContract.MovieData.COLUMN_MOVIE_ID + "=?"
                                    , new String[]{value.getAsString(MovieContract.MovieData.COLUMN_MOVIE_ID)});
                            numInserted++;
                            Log.e("SimilarMDDebug", "Insert Function Called : Updated : " + _id);
                        } else {
                            _id = db.update(MovieContract.MovieData.TABLE_MOVIE_DATA
                                    , value
                                    , MovieContract.MovieData.COLUMN_MOVIE_ID + " =? "
                                    , new String[]{value.getAsString(MovieContract.MovieData.COLUMN_MOVIE_ID)}
                            );
                            numInserted++;
                            Log.e("SimilarMDDebug", "Insert Function Called : Updated :Movie Data Overwritten : " + _id);
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            case MOVIE_CAST_DATA:
                numInserted = bulkInsertForTable(db, MovieContract.CastData.TABLE_CAST_DATA, values, uri);
                break;
            case MOVIE_CREW_DATA:
                numInserted = bulkInsertForTable(db, MovieContract.CrewData.TABLE_CREW_DATA, values, uri);
                break;
            case MOVIE_CAST_DATA_ID:
                numInserted = bulkInsertForTable(db, MovieContract.CastData.TABLE_CAST_DATA, values, uri);
                break;
            case MOVIE_CREW_DATA_ID:
                numInserted = bulkInsertForTable(db, MovieContract.CrewData.TABLE_CREW_DATA, values, uri);
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        return numInserted;
    }

    int bulkInsertForTable(SQLiteDatabase db, String table, ContentValues[] values, Uri uri) {
        // allows for multiple transactions
        db.beginTransaction();

        // keep track of successful inserts
        int numInserted = 0;
        try {
            for (ContentValues value : values) {
                if (value == null) {
                    throw new IllegalArgumentException("Cannot have null content values");
                }
                long _id = -1;
                try {
                    _id = db.insertOrThrow(table,
                            null, value);
                } catch (SQLiteConstraintException e) {
                    e.printStackTrace();
                }
                if (_id != -1) {
                    numInserted++;
                    Log.e("InsertDebug", "Inserting : " + numInserted);
                }
            }
            if (numInserted > 0) {
                // If no errors, declare a successful transaction.
                // database will not populate if this is not called
                db.setTransactionSuccessful();
            }
        } finally {
            // all transactions occur at once
            db.endTransaction();
        }
        if (numInserted > 0) {
            // if there was successful insertion, notify the content resolver that there
            // was a change
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numInserted;
    }
}
