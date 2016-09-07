package com.sreesha.android.moviebuzz.DataHandlerClasses;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Sreesha on 10-02-2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.sreesha.android.moviebuzz";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE_TRAILERS = "movieTrailers";
    public static final String PATH_MOVIE_REVIEWS = "movieReviews";
    public static final String PATH_MOVIE_GENRES = "movieGenres";
    public static final String PATH_MOVIE_FAVOURITES = "movieFavourites";
    public static final String PATH_FAVOURITES_MOVIE_DATA = "favouritesMovieData";
    public static final String PATH_MOVIE_CAST_DATA = "movieCast";
    public static final String PATH_MOVIE_CREW_DATA = "movieCrew";

    public static final class MovieData implements BaseColumns {
        public static final String TABLE_MOVIE_DATA = "movieData";

        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_PAGE = "page";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_LANGUAGE = "en";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_GENRE_IDS = "genreIDs";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_SIMILAR_TO_ID = "similarToMovieID";

        public static final String COLUMN_IS_MOVIE_FAVOURED = "isTheMovieFavoured";
        public static final String COLUMN_POSTER_IMAGE_BLOB = "posterImageBLOB";
        public static final String COLUMN_BACKDROP_IMAGE_BLOB = "backdropImageBLOB";
        /*
           0 - Popular Movies
           1 - HighestRatedMovies
           2 - Upcoming Movies
           3 - Movies Playing Now
           4 - movie_id (4Digit Integer) - Similar Movies
         */
        public static final String COLUMN_MOVIE_TYPE = "movieType";

        public static final int POPULAR_MOVIE_TYPE = 0;
        public static final int HIGHEST_RATED_MOVIE_TYPE = 1;
        public static final int UPCOMING_MOVIE_TYPE = 2;
        public static final int NOW_PLAYING_MOVIE_TYPE = 3;
        public static final int SIMILAR_MOVIE_TYPE = 4;
        public static final int SEARCHED_MOVIE_TYPE = 5;
        public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIES)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIES;
        ;

        public static Uri buildMovieDataUri(long id) {
            return ContentUris.withAppendedId(MOVIE_CONTENT_URI, id);
        }
    }

    public static final class WatchedMovieData implements BaseColumns {
        public static final String TABLE_WATCHED_MOVIE_DATA = "toWatch";
    }

    public static final class ToWatchMovieData implements BaseColumns {
        public static final String TABLE_TO_WATCH_MOVIE_DATA = "toWatch";
    }

    public static final class MovieTrailers implements BaseColumns {

        public static final String TABLE_MOVIE_TRAILERS = "MovieTrailerData";

        public static final String COLUMN_MOVIE_ID = "MovieId";
        public static final String COLUMN_TRAILER_ID = "TrailerId";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_TRAILER_KEY = "TrailerKey";
        public static final String COLUMN_TRAILER_NAME = "TrailerName";
        public static final String COLUMN_SITE = "TrailerSite";
        public static final String COLUMN_SIZE = "Size";
        public static final String COLUMN_TRAILER_TYPE = "TrailerType";

        public static final Uri TRAILER_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE_TRAILERS)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + TABLE_MOVIE_TRAILERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + TABLE_MOVIE_TRAILERS;

        public static Uri buildTrailerDataUri(long id) {
            return ContentUris.withAppendedId(TRAILER_CONTENT_URI, id);
        }

        public static Uri buildMovieTrailerUriWithMovieId(String movieId) {
            return TRAILER_CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
    }

    public static final class MovieReviews implements BaseColumns {
        public static final String TABLE_MOVIE_REVIEWS = "MovieReviews";
        public static final String COLUMN_MOVIE_ID = "MovieId";
        public static final String COLUMN_PAGE = "Page";
        public static final String COLUMN_REVIEW_ID = "ReviewId";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_REVIEW_CONTENT = "Content";
        public static final String COLUMN_REVIEW_URL = "ReviewURL";
        public static final String COLUMN_TOTAL_PAGES = "TotalPageNumber";

        public static final Uri REVIEWS_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE_REVIEWS)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_REVIEWS;

        public static Uri buildReviewDataUri(long id) {
            return ContentUris.withAppendedId(REVIEWS_CONTENT_URI, id);
        }

        public static Uri buildMovieReviewUriWithMovieId(String movieId) {
            return REVIEWS_CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
    }

    public static final class UserFavourite implements BaseColumns {
        public static final String TABLE_FAVOURITE_DATA = "FavouritesData";

        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TYPE = "movieTypeColumn";
        public static final String COLUMN_PAGE = "page";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_LANGUAGE = "en";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final int FAVOURITE_MOVIE_TYPE = 0;
        public static final int TO_WATCH_MOVIE_TYPE = 1;
        public static final int WATCHED_MOVIE_TYPE = 2;

        public static final Uri FAVOURITES_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE_FAVOURITES)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_FAVOURITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_FAVOURITES;

        public static Uri buildFavouriteDataUri(long id) {
            return ContentUris.withAppendedId(FAVOURITES_CONTENT_URI, id);
        }

        public static Uri buildFavouritesUriWithMovieId(String MovieId) {
            return FAVOURITES_CONTENT_URI.buildUpon().appendPath(MovieId).build();
        }
    }

    public static final class CastData implements BaseColumns {
        public static final String TABLE_CAST_DATA = "CastTable";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_CAST_ID = "castID";
        public static final String COLUMN_CHARACTER = "character";
        public static final String COLUMN_CREDIT_ID = "creditID";
        public static final String COLUMN_UNKNOWN_ID = "unknownID";
        public static final String COLUMN_NAME = "castName";
        public static final String COLUMN_ORDER = "castOrder";
        public static final String COLUMN_PROFILE_PICTURE_PATH = "picturePath";
        public static final String COLUMN_POPULARITY = "popularity";

        public static final Uri MOVIE_CAST_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE_CAST_DATA)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_CAST_DATA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_CAST_DATA;

        public static Uri buildCastDataUri(long id) {
            return ContentUris.withAppendedId(MOVIE_CAST_CONTENT_URI, id);
        }

        public static Uri buildCastUriWithMovieId(String MovieId) {
            return MOVIE_CAST_CONTENT_URI.buildUpon().appendPath(MovieId).build();
        }
    }

    public static final class CrewData implements BaseColumns {
        public static final String TABLE_CREW_DATA = "CrewTable";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_JOB = "job";
        public static final String COLUMN_CREDIT_ID = "creditID";
        public static final String COLUMN_UNKNOWN_ID = "unknownID";
        public static final String COLUMN_NAME = "castName";
        public static final String COLUMN_PROFILE_PICTURE_PATH = "picturePath";
        public static final String COLUMN_DEPARTMENT = "department";

        public static final Uri MOVIE_CREW_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE_CREW_DATA)
                .build();
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_CREW_DATA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + '/'
                + CONTENT_AUTHORITY
                + '/'
                + PATH_MOVIE_CREW_DATA;

        public static Uri buildCastDataUri(long id) {
            return ContentUris.withAppendedId(MOVIE_CREW_CONTENT_URI, id);
        }

        public static Uri buildCastUriWithMovieId(String MovieId) {
            return MOVIE_CREW_CONTENT_URI.buildUpon().appendPath(MovieId).build();
        }
    }
}
