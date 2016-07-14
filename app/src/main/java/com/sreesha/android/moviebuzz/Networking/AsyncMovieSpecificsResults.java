package com.sreesha.android.moviebuzz.Networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sreesha on 14-05-2016.
 */
public abstract class AsyncMovieSpecificsResults {
    protected abstract void onResultJSON(JSONObject object) throws JSONException;

    protected abstract void onResultString(String stringObject, String errorString, String parseStatus);

    protected abstract void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList);

    protected abstract void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList);

    protected abstract void onResultParsedIntoPopularPersonInfo(ArrayList<PopularPeopleInstance> popularPeopleInstanceArrayList);

    protected abstract void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList
            , ArrayList<MovieImage> posterList
    );

    protected abstract void onResultParsedIntoPersonData(PersonInstance instance);
}
