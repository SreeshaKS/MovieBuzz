package com.sreesha.android.moviebuzz.Networking;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import android.database.SQLException;

import java.util.ArrayList;

/**
 * Created by Sreesha on 28-01-2016.
 */
public class DownloadData extends AsyncTask<String, Void, String> {
    AsyncResult callback;
    ProgressDialog mProgressDialog;
    Context context;
    JSONObject result;
    Boolean progressDialogSet = false;
    ArrayList<ContentValues> contentValueMovieArrayList = new ArrayList<>();

    private boolean downloadingMovieData = false;
    ArrayList<MovieDataInstance> movieList;

    private boolean downloadingReviewsData = false;
    ArrayList<MovieReviewInstance> reviewsArrayList;

    private boolean downloadingTrailersData = false;
    ArrayList<MovieTrailerInstance> trailersArrayList;

    boolean isResultEmptyDataSet = false;
    public static final String RESULT_EMPTY_DATA_SET = "emptyResultSet";

    int movieType = 0;
    String globalURLVariable;

    public DownloadData(AsyncResult callback) {
        this.callback = callback;
    }

    public DownloadData(AsyncResult callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (context != null) {
            progressDialogSet = true;
            mProgressDialog = new ProgressDialog(context);
            //mProgressDialog.show();
            mProgressDialog.setMessage("FetchingData ......Please Wait !! ");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        if (Utility.getConnectionTypeStatus(context).equals(Utility.CONNECTIVITY_CONNECTING_CONNECTED)) {
            String stringData = null;
            try {
                if (urls[0] != null) {
                    stringData = downloadUrl(urls[0]);
                    convertToJSONAndParseJSON(stringData, urls[0]);
                }
                if (urls[1] != null) {
                    stringData = downloadUrl(urls[1]);
                    convertToJSONAndParseJSON(stringData, urls[1]);
                }
                return stringData;
            } catch (IOException e) {
                return "Unable to download the requested page.";
            } catch (ArrayIndexOutOfBoundsException e) {
                return stringData;
            }
        } else {
            return Utility.CONNECTIVITY_NOT_CONNECTED;
        }
    }

    private void convertToJSONAndParseJSON(String stringData, String URL) {
        try {
            globalURLVariable = URL;
            result = new JSONObject(stringData);
            if (URL.contains(APIUrls.buildPopularMoviesURL().toString())
                    || URL.contains(APIUrls.buildUpcomingMoviesURL().toString())
                    || URL.contains(APIUrls.buildPlayingNowMoviesURL().toString())
                    || URL.contains("similar")
                    || URL.contains("search")) {
                if (URL.contains(APIUrls.buildPopularMoviesURL().toString())) {
                    Uri uri = Uri.parse(URL);
                    if (uri.getQueryParameter(APIUrls.API_SORT_PARAM).equals(APIUrls.API_SORT_POPULARITY_DESC)) {
                        movieType = MovieContract.MovieData.POPULAR_MOVIE_TYPE;
                    } else {
                        movieType = MovieContract.MovieData.HIGHEST_RATED_MOVIE_TYPE;
                    }
                } else if (URL.contains(APIUrls.buildUpcomingMoviesURL().toString())) {
                    movieType = MovieContract.MovieData.UPCOMING_MOVIE_TYPE;
                } else if (URL.contains(APIUrls.buildPlayingNowMoviesURL().toString())) {
                    movieType = MovieContract.MovieData.NOW_PLAYING_MOVIE_TYPE;
                } else if (URL.contains("similar")) {
                    movieType = MovieContract.MovieData.SIMILAR_MOVIE_TYPE;
                } else if (URL.contains("search")) {
                    movieType = MovieContract.MovieData.SEARCHED_MOVIE_TYPE;
                }
                downloadingMovieData = true;
                downloadingReviewsData = false;
                downloadingTrailersData = false;
                parseMovieData();
            }

            if (URL.contains(APIUrls.getPathReview())) {
                downloadingMovieData = false;
                downloadingReviewsData = true;
                downloadingTrailersData = false;
                parseReviewData();
            }
            if (URL.contains(APIUrls.getPathVideo())) {
                downloadingMovieData = false;
                downloadingReviewsData = false;
                downloadingTrailersData = true;
                parseTrailerData();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onResultString(stringData);
        }
    }

    private void parseReviewData() throws JSONException {
        long movie_id = result.getInt("id");
        int total_pages = result.getInt("id");
        int page_no = result.getInt("total_pages");
        if (total_pages != 0) {
            JSONArray reviewsJSONArray = result.getJSONArray("results");


            reviewsArrayList = new ArrayList<>();
            contentValueMovieArrayList = new ArrayList<>();
            for (int i = 0; i < reviewsJSONArray.length(); i++) {
                JSONObject obj = reviewsJSONArray.getJSONObject(i);
                reviewsArrayList.add(
                        new MovieReviewInstance(
                                movie_id
                                , page_no
                                , obj.getString("id")
                                , obj.getString("author")
                                , obj.getString("content")
                                , obj.getString("url")
                                , total_pages
                        )
                );

                contentValueMovieArrayList.add(
                        getContentValuesFromReviewsInstance(new MovieReviewInstance(
                                        movie_id
                                        , page_no
                                        , obj.getString("id")
                                        , obj.getString("author")
                                        , obj.getString("content")
                                        , obj.getString("url")
                                        , total_pages
                                )
                        )
                );
            }
            try {
                insertDataIntoSQLiteDatabase(contentValueMovieArrayList, MovieContract.MovieReviews.REVIEWS_CONTENT_URI);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Log.e("DownloadData-Reviews", result.toString());
            publishProgress();
        } else {
            isResultEmptyDataSet = true;
        }
    }

    private void parseTrailerData() throws JSONException {
        long movie_id = result.getInt("id");
        JSONArray trailerJSONArray = result.getJSONArray("results");

        trailersArrayList = new ArrayList<>();
        contentValueMovieArrayList = new ArrayList<>();
        for (int i = 0; i < trailerJSONArray.length(); i++) {
            JSONObject obj = trailerJSONArray.getJSONObject(i);
            trailersArrayList.add(
                    new MovieTrailerInstance(
                            movie_id
                            , obj.getString("id")
                            , obj.getString("key")
                            , obj.getString("iso_639_1")
                            , obj.getString("name")
                            , obj.getString("site")
                            , obj.getInt("size")
                            , obj.getString("type")
                    )
            );
            contentValueMovieArrayList.add(
                    getContentValuesFromTrailerInstance(new MovieTrailerInstance(
                                    movie_id
                                    , obj.getString("id")
                                    , obj.getString("key")
                                    , obj.getString("iso_639_1")
                                    , obj.getString("name")
                                    , obj.getString("site")
                                    , obj.getInt("size")
                                    , obj.getString("type")
                            )
                    )
            );
        }
        try {
            insertDataIntoSQLiteDatabase(contentValueMovieArrayList, MovieContract.MovieTrailers.TRAILER_CONTENT_URI);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        publishProgress();
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
            String genreJSONArrayString = obj.getJSONArray("genre_ids").toString();
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
                    , genreJSONArrayString
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

    public static MovieDataInstance getMovieDataInstanceFromJSON
            (JSONObject obj
                    , int page_no
                    , int total_results
                    , int total_pages
            ) throws JSONException {
        String genreJSONArrayString = obj.getJSONArray("genre_ids").toString();
        boolean adult = false;
        try {
            adult = obj.getBoolean("adult");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new MovieDataInstance(
                page_no
                , total_results
                , total_pages
                , obj.getString("poster_path")
                , adult
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
                , genreJSONArrayString
        );
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (downloadingMovieData) {
        } else if (downloadingReviewsData) {
            if (!isResultEmptyDataSet) {
                callback.onReviewsResultParsed(reviewsArrayList);
            } else {
                callback.onResultString(RESULT_EMPTY_DATA_SET);
            }
        } else if (downloadingTrailersData) {
            callback.onTrailersResultParsed(trailersArrayList);
        }
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
        //values.put(MovieContract.MovieData.COLUMN_MOVIE_ID, m.getMovieID());
        values.put(MovieContract.MovieData.COLUMN_VOTE_AVERAGE, m.getAverageVoting());
        if (movieType != MovieContract.MovieData.SIMILAR_MOVIE_TYPE)
            values.put(MovieContract.MovieData.COLUMN_MOVIE_TYPE, movieType);
        else {
            Uri uri = Uri.parse(globalURLVariable);
            values.put(MovieContract.MovieData.COLUMN_SIMILAR_TO_ID
                    ,/*Get the movie id from the path list element at position 2*/
                    Integer.parseInt(uri.getPathSegments().get(2))
            );
            values.put(MovieContract.MovieData.COLUMN_MOVIE_TYPE
                    ,/*Set Movie Type*/
                    movieType);
        }
        values.put(MovieContract.MovieData.COLUMN_GENRE_IDS, m.getGENRE_ID_JSON_ARRAY_STRING());
        return values;
    }

    private ContentValues getContentValuesFromTrailerInstance(MovieTrailerInstance movieTrailerInstance) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieTrailers.COLUMN_MOVIE_ID, movieTrailerInstance.getMOVIE_ID());
        values.put(MovieContract.MovieTrailers.COLUMN_TRAILER_ID, movieTrailerInstance.getTRAILER_ID());
        values.put(MovieContract.MovieTrailers.COLUMN_ISO_639_1, movieTrailerInstance.getISO_639_1());
        values.put(MovieContract.MovieTrailers.COLUMN_TRAILER_KEY, movieTrailerInstance.getTRAILER_KEY());
        values.put(MovieContract.MovieTrailers.COLUMN_TRAILER_NAME, movieTrailerInstance.getTRAILER_NAME());
        values.put(MovieContract.MovieTrailers.COLUMN_SITE, movieTrailerInstance.getSITE());
        values.put(MovieContract.MovieTrailers.COLUMN_SIZE, movieTrailerInstance.getSIZE());
        values.put(MovieContract.MovieTrailers.COLUMN_TRAILER_TYPE, movieTrailerInstance.getTRAILER_TYPE());
        return values;
    }

    private ContentValues getContentValuesFromReviewsInstance(MovieReviewInstance movieReviewsInstance) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieReviews.COLUMN_MOVIE_ID, movieReviewsInstance.getMOVIE_ID());
        values.put(MovieContract.MovieReviews.COLUMN_PAGE, movieReviewsInstance.getPAGE());
        values.put(MovieContract.MovieReviews.COLUMN_REVIEW_ID, movieReviewsInstance.getREVIEW_ID());
        values.put(MovieContract.MovieReviews.COLUMN_AUTHOR, movieReviewsInstance.getAUTHOR());
        values.put(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT, movieReviewsInstance.getREVIEW_CONTENT());
        values.put(MovieContract.MovieReviews.COLUMN_REVIEW_URL, movieReviewsInstance.getREVIEW_URL());
        values.put(MovieContract.MovieReviews.COLUMN_TOTAL_PAGES, movieReviewsInstance.getTOTAL_PAGES());
        return values;
    }

    public static int[] convertToIntegerArray(JSONArray genre_ids) {
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

    @Override
    protected void onPostExecute(String s) {
        if (s.equals(Utility.CONNECTIVITY_NOT_CONNECTED)) {
            callback.onResultString(s);
            if (progressDialogSet) {
                mProgressDialog.dismiss();
            }
        } else if (movieList != null && movieList.size() != 0) {
            callback.onResultParsedIntoMovieList(movieList);
            if (progressDialogSet) {
                mProgressDialog.dismiss();
            }
        }
        super.onPostExecute(s);
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

    public static String convertStreamToString(InputStream is) {
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
