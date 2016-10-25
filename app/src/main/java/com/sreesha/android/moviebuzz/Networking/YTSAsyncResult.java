package com.sreesha.android.moviebuzz.Networking;

import com.sreesha.android.moviebuzz.RSSFeed.YTSMovie;
import com.sreesha.android.moviebuzz.RSSFeed.YTSTorrent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sreesha on 18-10-2016.
 */

public interface YTSAsyncResult {
    void onMovieDataParsed(ArrayList<YTSMovie> YTSMovieArrayList);
    void onResultJSON(JSONObject object) throws JSONException;
    void onResultString(String stringObject, String errorString, String parseStatus);
}
