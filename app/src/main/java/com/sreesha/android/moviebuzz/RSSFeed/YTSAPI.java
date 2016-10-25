package com.sreesha.android.moviebuzz.RSSFeed;

import android.net.Uri;

/**
 * Created by Sreesha on 18-10-2016.
 */

public class YTSAPI {
    /*
        Documentation  : https://yts.ag/api#list_movies
     */

    /*API meta data*/
    private static final String AUTHORITY = "yts.ag";
    private static final String PATH_API = "api";
    private static final String PATH_VERSION = "v2";
    private static final String PATH_RESOURCE_TYPE = "list_movies.json";
    /*API PARAMS*/
    public static final String PARAM_QUALITY = "quality";
    public static final String PARAM_GENRE = "genre";
    public static final String PARAM_RATING = "minimum_rating";
    public static final String PARAM_QUERY_TERM = "query_term";
    public static final String PARAM_RT_RATINGS = "with_rt_ratings";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SORT = "sort_by";
    /*API PARAM VALUES*/
    public static final String QUALITY_1080p = "1080p";
    public static final String QUALITY_720p = "720p";
    public static final String QUALITY_3D = "3D";
    public static final String SORT_TITLE = "title";
    public static final String SORT_YEAR = "year";
    public static final String SORT_RATING = "rating";
    public static final String SORT_PEERS = "peers";
    public static final String SORT_SEEDS = "seeds";
    public static final String SORT_DOWNLOAD_COUNT = "download_count";
    public static final String SORT_LIKE_COUNT = "like_count";
    public static final String SORT_DATE_ADDED = "date_added";

    public static Uri.Builder buildMovieListBaseURI() {
        return new Uri.Builder()
                .scheme("https")
                .authority(AUTHORITY)
                .appendPath(PATH_API)
                .appendPath(PATH_VERSION)
                .appendPath(PATH_RESOURCE_TYPE);
    }
}
