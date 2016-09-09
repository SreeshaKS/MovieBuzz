package com.sreesha.android.moviebuzz.Networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sreesha.android.moviebuzz.BuildConfig;
import com.sreesha.android.moviebuzz.Settings.MovieBuzzApplication;
import com.sreesha.android.moviebuzz.R;

/**
 * Created by Sreesha on 28-01-2016.
 */
public class APIUrls {

    private static final String SCHEME = "http";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String PATH_1 = "3";
    private static final String PATH_2 = "movie";
    private static final String PATH_3 = "500";
    private static final String PATH_4 = "discover";
    private static final String PATH_5 = "movie";


    public static final String FACEBOOK_URL = "https://www.facebook.com/NDmoviebuzz";

    public static String getPathVideo() {
        return PATH_VIDEO;
    }

    private static final String PATH_VIDEO = "videos";

    public static String getPathReview() {
        return PATH_REVIEW;
    }

    private static final String PATH_REVIEW = "reviews";

    private static final String API_KEY = BuildConfig.API_KEY;

    public static final String WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID;

    public static final String API_PAGE_PARAM = "page";
    private static final String API_KEY_PARAM = "api_key";
    public static final String API_SORT_PARAM = "sort_by";
    public static final String API_SORT_POPULARITY_DESC = "popularity.desc";
    public static final String API_HIGHEST_RATING_DESC = "vote_average.desc";
    public static final String API_LANG_PARAM = "language";

    public static final String API_IMG_W_92 = "w92";
    public static final String API_IMG_W_154 = "w154";
    public static final String API_IMG_W_185 = "w185";
    public static final String API_IMG_W_342 = "w342";
    public static final String API_IMG_W_500 = "w500";
    public static final String API_IMG_W_780 = "w780";
    public static final String API_IMG_W_1920 = "w1920";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p";
    private static final String IMAGE_AUTHORITY = "image.tmdb.org";
    private static final String IMAGE_PATH_1 = "t";
    private static final String IMAGE_PATH_2 = "p";


    public final static String API_URL_NOW_PLAYING = "http://api.themoviedb.org/3/movie/now_playing?"
            + "api_key=" + API_KEY;

    public final static String API_URL_UPCOMING_MOVIES = "http://api.themoviedb.org/3/movie/upcoming?"
            + "api_key=" + API_KEY;


    public final static String API_URL_HIGHEST_RATED = "http://api.themoviedb.org/3/movie/top_rated?"
            + "api_key=" + API_KEY;

    public final static String API_URL_MOST_POPULAR = "http://api.themoviedb.org/3/movie/popular?"
            + "api_key=" + API_KEY;

    public final static String API_URL_CAST_AND_CREW_BASE_URL = "http://api.themoviedb.org/3/movie/id/credits";

    public static Uri.Builder buildCastCrewCreditsURL(String movieID) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieID)
                .appendPath("credits")
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildPersonURL(String castCrewID) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("person")
                .appendPath(castCrewID)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildBaseMovieDBURL() {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath(PATH_3)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildPopularMoviesURL() {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        Log.d("MyApplication", langConst);
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_4)
                .appendPath(PATH_5)
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildUpcomingMoviesURL() {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        Log.d("MyApplication", langConst);
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath("upcoming")
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildPlayingNowMoviesURL() {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath("now_playing")
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }


    public static Uri.Builder buildBaseImageURL() {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(IMAGE_AUTHORITY)
                .appendPath(IMAGE_PATH_1)
                .appendPath(IMAGE_PATH_2));
    }

    public static Uri.Builder buildMovieTrailerURL(String movieID) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2))
                .appendPath(movieID)
                .appendPath(PATH_VIDEO)
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildSimilarMoviesURL(String movieID) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie"))
                .appendPath(movieID)
                .appendPath("similar")
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildMoviePhotosURL(String movieID) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie"))
                .appendPath(movieID)
                .appendPath("images")
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }
    public static Uri.Builder buildPersonPhotosURL(String personID) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("person"))
                .appendPath(personID)
                .appendPath("images")
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildTaggedImagesURL(String personID) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("person"))
                .appendPath(personID)
                .appendPath("tagged_images")
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }
    public static Uri.Builder buildMovieImageURL(String resolution, String filePath) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("t")
                .appendPath("p")
                .appendPath(resolution)
                .appendEncodedPath(filePath.split("/")[1])
                .appendQueryParameter(API_KEY_PARAM, API_KEY));
    }

    public static Uri.Builder buildMovieReviewsURL(String movieID) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2))
                .appendPath(movieID)
                .appendPath(PATH_REVIEW)
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildYoutubeThumbNailURL(String trailerKey, final String resolution) {
       /* http://img.youtube.com/vi/7jIBCiYg58k/mqdefault.jpg*/
        switch (resolution) {
            case "d":
                return (new Uri.Builder()
                        .scheme(SCHEME)
                        .authority("img.youtube.com")
                        .appendPath("vi")
                        .appendPath(trailerKey)
                        .appendPath("default" + ".jpg"));
            default:
                /*if its sd, hq, mq or maxres*/
                return (new Uri.Builder()
                        .scheme(SCHEME)
                        .authority("img.youtube.com")
                        .appendPath("vi")
                        .appendPath(trailerKey)
                        .appendPath(resolution + "default" + ".jpg"));
        }
    }

    public static Uri.Builder buildYoutubeTrailerURI(String trailerKey) {
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", trailerKey));
    }

    public static Uri.Builder buildPopularCastCrewURL() {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("person"))
                .appendPath("popular")
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildPersonDetailsURL(long id) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("person"))
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
    }

    public static Uri.Builder buildSearchURL(String queryString) {
        Context context = MovieBuzzApplication.getAppContext();
        String langConst = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(context))
                .getString(context.getString(R.string.preferences_user_language_key), "en");
        return (new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("search"))
                .appendPath("movie")
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(API_LANG_PARAM, langConst)
                .appendQueryParameter("query", queryString);
    }
}
