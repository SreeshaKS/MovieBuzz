package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieDataDBHelper;

/**
 * Created by Sreesha on 14-04-2016.
 */
public class SearchHelper {
    private MovieDataDBHelper dataBaseHelper;

    public SearchHelper(Context context) {
        dataBaseHelper = new MovieDataDBHelper(context);
    }

    public Cursor getMovieListByKeyword(String search) {
        //Open connection to read only
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " +
                        MovieContract.MovieData.TABLE_MOVIE_DATA
                        +
                        " WHERE lower(" + MovieContract.MovieData.COLUMN_MOVIE_TITLE+")"
                        + " LIKE lower('% " + search + " %'"+")"
                        + " OR "
                        + "lower("+MovieContract.MovieData.COLUMN_ORIGINAL_TITLE+")"
                        + " LIKE lower('% " + search + " %'"+")";
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
