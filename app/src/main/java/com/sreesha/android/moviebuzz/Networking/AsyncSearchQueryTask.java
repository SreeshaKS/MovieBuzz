package com.sreesha.android.moviebuzz.Networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sreesha on 15-04-2016.
 */
public class AsyncSearchQueryTask extends AsyncTask<String, Void, String> {
    AsyncResult callback;
    Context context;
    JSONObject result;
    ArrayList<ContentValues> contentValueMovieArrayList = new ArrayList<>();

    private boolean downloadingMovieData = false;
    ArrayList<MovieDataInstance> movieList;

    private boolean downloadingReviewsData = false;
    ArrayList<MovieReviewInstance> reviewsArrayList;

    private boolean downloadingTrailersData = false;
    ArrayList<MovieTrailerInstance> trailersArrayList;

    boolean isResultEmptyDataSet = false;
    public static final String RESULT_EMPTY_DATA_SET = "emptyResultSet";

    public AsyncSearchQueryTask(AsyncResult callback) {
        this.callback = callback;
    }

    public AsyncSearchQueryTask(AsyncResult callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        if(Utility.getConnectionTypeStatus(context).equals(Utility.CONNECTIVITY_CONNECTING_CONNECTED)) {
            try {
                String stringData=null;
                stringData = downloadUrl(urls[0]);
                convertToJSONAndParseJSON(stringData, urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return Utility.CONNECTIVITY_NOT_CONNECTED;
        }
        return null;
    }
    private void convertToJSONAndParseJSON(String stringData, String URL) {
        try {
            result = new JSONObject(stringData);
            parseMovieData();
        }catch (JSONException e){
            e.printStackTrace();
            callback.onResultString(stringData);
        }
    }
    private void parseMovieData() throws JSONException {
        movieList = new ArrayList<>();
        int page_no = result.getInt("page");
        int total_results = result.getInt("total_results");
        int total_pages = result.getInt("total_pages");
        callback.onResultJSON(result);
        JSONArray movieResultArray = result.getJSONArray("results");
        contentValueMovieArrayList = new ArrayList<>();
        for (int i = 0; i < movieResultArray.length(); i++) {
            JSONObject obj = movieResultArray.getJSONObject(i);
            MovieDataInstance movieInstance = new MovieDataInstance(
                    page_no
                    , total_results
                    , total_pages
                    , obj.getString("poster_path")
                    , obj.getBoolean("adult")
                    , obj.getString("overview")
                    , obj.getString("release_date")
                    , convertToIntegerArray(obj.getJSONArray("genre_ids"))
                    , obj.getInt("id")
                    , obj.getString("original_title")
                    , obj.getString("original_language")
                    , obj.getString("title")
                    , obj.getString("backdrop_path")
                    , obj.getDouble("popularity")
                    , obj.getInt("vote_count")
                    , obj.getBoolean("video")
                    , obj.getDouble("vote_average")
                    ,obj.getString("genre_ids")
            );
            movieList.add(movieInstance);
            contentValueMovieArrayList.add(
                    getContentValuesFromMovieInstance(movieInstance)
            );
        }
        try {
            insertDataIntoSQLiteDatabase(contentValueMovieArrayList, MovieContract.MovieData.MOVIE_CONTENT_URI);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private int[] convertToIntegerArray(JSONArray genre_ids) {
        int[] arr = new int[genre_ids.length()];
        try {
            for (int i = 0; i < genre_ids.length(); i++) {
                arr[i] = genre_ids.getInt(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            arr = new int[]{0, 0, 0, 0};
        }
        return arr;
    }
    private void insertDataIntoSQLiteDatabase(ArrayList<ContentValues> contentValueMovieArrayList
            , Uri contentURI) throws SQLException {
        int inserted = 0;
        ContentValues[] valueArray = new ContentValues[contentValueMovieArrayList.size()];
        contentValueMovieArrayList.toArray(valueArray);
        if (contentValueMovieArrayList.size() > 0) {
            inserted = context.getContentResolver()
                    .bulkInsert(
                            contentURI
                            , valueArray
                    );
            Log.e("Inserted : \t", String.valueOf(inserted));
        }
    }
    private ContentValues getContentValuesFromMovieInstance(MovieDataInstance m) {

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieData.COLUMN_MOVIE_ID, m.getMovieID());
        values.put(MovieContract.MovieData.COLUMN_PAGE, m.getPageNumber());
        values.put(MovieContract.MovieData.COLUMN_POSTER_PATH, m.getPOSTER_PATH());
        values.put(MovieContract.MovieData.COLUMN_ADULT, m.getCertification() ? 1 : 0);
        values.put(MovieContract.MovieData.COLUMN_OVERVIEW, m.getOverView());
        values.put(MovieContract.MovieData.COLUMN_RELEASE_DATE, m.getReleaseDate());
        values.put(MovieContract.MovieData.COLUMN_ORIGINAL_TITLE, m.getOriginalTitle());
        values.put(MovieContract.MovieData.COLUMN_LANGUAGE, m.getOriginalLanguage());
        values.put(MovieContract.MovieData.COLUMN_MOVIE_TITLE, m.getTitle());
        values.put(MovieContract.MovieData.COLUMN_BACKDROP_PATH, m.getBackDropPath());
        values.put(MovieContract.MovieData.COLUMN_POPULARITY, m.getPopularity());
        values.put(MovieContract.MovieData.COLUMN_VOTE_COUNT, m.getVoteCount());
        values.put(MovieContract.MovieData.COLUMN_VIDEO, m.getVideoBool() ? 1 : 0);
        values.put(MovieContract.MovieData.COLUMN_MOVIE_ID, m.getMovieID());
        values.put(MovieContract.MovieData.COLUMN_VOTE_AVERAGE, m.getAverageVoting());

        return values;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.equals(Utility.CONNECTIVITY_NOT_CONNECTED)) {
            callback.onResultParsedIntoMovieList(movieList);
        }else{
            callback.onResultString(s);
        }
    }
    private String downloadUrl(String urlString) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.i("HTTP-RESPONSE-CODE", "" + responseCode);
            is = conn.getInputStream();

            return convertStreamToString(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
