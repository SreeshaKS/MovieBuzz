package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import com.sreesha.android.moviebuzz.Networking.MovieImage;

import java.util.ArrayList;

/**
 * Created by Sreesha on 10-04-2016.
 */
public interface UIUpdatableInterface {
    String REVIEWS_FRAGMENT="reviewsFragment";
    String MOVIE_DETAIL_FRAGMENT="movieDetailFragment";
    String TRAILERS_FRAGMENT="movieTrailerFragment";
    String MOVIE_DETAIL_TABS_FRAGMENT = "movieDetailTabsFragment";
    String MOVIE_CAST_TABS_FRAGMENT = "movieCastTabsFragment";
    String MOVIE_CREW_TABS_FRAGMENT = "movieCrewTabsFragment";
    String SIMILAR_MOVIES_FRAGMENT = "similarMoviesFragment";
    String MOVIE_PHOTOS_FRAGMENT = "moviePhotosFragment";
    void OnUIReadyToBeUpdated(String fragmentName);
    void OnMovieImageDataLoaded(ArrayList<MovieImage> mMovieImageArrayList);
}
