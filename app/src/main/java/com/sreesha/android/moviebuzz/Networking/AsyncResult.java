package com.sreesha.android.moviebuzz.Networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sreesha on 28-10-2015.
 */
public interface AsyncResult {
    void onResultJSON(JSONObject object) throws JSONException;

    void onResultString(String stringObject);

    void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList);

    void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList);

    void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList);
}
