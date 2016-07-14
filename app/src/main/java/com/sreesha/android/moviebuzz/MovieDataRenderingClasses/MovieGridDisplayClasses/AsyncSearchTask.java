package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;

import java.util.ArrayList;

/**
 * Created by Sreesha on 16-04-2016.
 */
public class AsyncSearchTask extends AsyncTask<String, MovieDataInstance, String> {

    String searchString = null;
    private boolean cancelAsyncTask = false;
    Cursor searchCursor;
    ArrayList<MovieDataInstance> mMovieList = new ArrayList<>();
    SearchResultDispatchInterface callBack;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        for (; ; ) {
            if (cancelAsyncTask) {
                cancel(false);
                Log.e("SearchDebug", "Canceling Async Task");
                return null;
            }
            if (searchString != null && mMovieList != null && mMovieList.size() != 0) {
                for (MovieDataInstance instance : mMovieList) {
                    Log.e("SearchDebug", "Running Search");
                    if (instance.getTitle().toLowerCase().contains(searchString.toLowerCase())
                            || instance.getOriginalTitle().toLowerCase().contains(searchString.toLowerCase())) {
                        Log.e("SearchDebug", "ResultFound : Title" + instance.getTitle());
                        publishProgress(instance);
                    }
                }
                cancel(false);
            }
        }
    }

    public void notifyDataSetChanged(ArrayList<MovieDataInstance> asyncMovieList) {
        mMovieList.clear();
        mMovieList = asyncMovieList;
    }

    public void updateSearchString(String newString) {
        searchString = newString;
    }

    public void cancelSearchProcess() {
        cancelAsyncTask = true;
    }

    @Override
    protected void onProgressUpdate(MovieDataInstance... values) {
        if (callBack != null) {
            callBack.onSearchResultAcquired(values[0]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public interface SearchResultDispatchInterface {
        void onSearchResultAcquired(MovieDataInstance instance);
    }

    public void setSearchResultCallBackInterface(SearchResultDispatchInterface interfaceCallBack) {
        callBack = interfaceCallBack;
    }
}
