package com.sreesha.android.moviebuzz.Networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay.PersonImage;
import com.sreesha.android.moviebuzz.RSSFeed.YTSMovie;
import com.sreesha.android.moviebuzz.RSSFeed.YTSTorrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sreesha on 14-05-2016.
 */
public class DownloadMovieSpecifics extends AsyncTask<String, Void, String> {

    private String ERROR_STRING = null;
    private String RESULT_STATUS = null;

    private String resultString = "defaultResult";

    private static String SUCCESS_STATUS = "parseDone";
    private static String ERROR_STATUS = "parsingError";

    private static String NETWORK_ERROR = "networkError";
    private static String JSON_PARSE_ERROR = "jsonParseError";
    private static String XML_PULL_PARSER = "xmlParseError";

    private static String SQLITE_CAST_INSERT_ERROR = "castDataInsertError";
    private static String SQLITE_CREW_INSERT_ERROR = "crewDataInsertError";

    private ArrayList<CastDataInstance> castDataArrayList;
    private ArrayList<CrewDataInstance> crewDataArrayList;
    private ArrayList<YTSMovie> mYTSMoviesArrayList;
    private ArrayList<PopularPeopleInstance> mPopularPeopleArrayList = new ArrayList<>();

    private ArrayList<MovieImage> mBackDropList;
    private ArrayList<MovieImage> mPosterList;

    private ArrayList<PersonImage> mPersonImageList;

    ArrayList<ContentValues> contentValueCastCrewArrayList = new ArrayList<>();
    private Context mContext;
    private AsyncMovieSpecificsResults mMovieSpecificResultNotifier;
    private YTSAsyncResult mYTSMovieResultNotifier;

    PersonInstance mRequestedPersonData;

    public DownloadMovieSpecifics(Context mContext, AsyncMovieSpecificsResults mMovieSpecificResultNotifier) {
        this.mContext = mContext;
        this.mMovieSpecificResultNotifier = mMovieSpecificResultNotifier;
    }

    public DownloadMovieSpecifics(Context mContext, YTSAsyncResult mYTSMovieResultNotifier) {
        this.mContext = mContext;
        this.mYTSMovieResultNotifier = mYTSMovieResultNotifier;
    }

    @Override
    protected String doInBackground(String... params) {
        for (String param : params) {
            try {
                //Parse YTS Movies
                if (param.contains("https://yts.ag/api/v2/")) {

                    resultString = downloadUrl(param);
                    Log.d("torrentDebug", resultString );
                    parseYTSRSSFeed(resultString);
                }

            } catch (IOException e) {
                e.printStackTrace();
                ERROR_STRING = NETWORK_ERROR;
                ERROR_STATUS = e.getMessage();
                return ERROR_STATUS;
            }
            try {
                //Parse Cast/Crew Credits Information
                if (param.contains("credits")) {
                    resultString = downloadUrl(param);
                    parseCastCrewCredits(resultString);
                }

            } catch (IOException e) {
                e.printStackTrace();
                ERROR_STRING = NETWORK_ERROR;
                ERROR_STATUS = e.getMessage();
                return ERROR_STATUS;
            }
            try {
                //Parse Cast/Crew Credits Information
                if (param.contains(APIUrls.buildBaseMovieDBURL("").toString())) {
                    resultString = downloadUrl(param);
                    parseExtendedMovieData(resultString);
                }

            } catch (IOException e) {
                e.printStackTrace();
                ERROR_STRING = NETWORK_ERROR;
                ERROR_STATUS = e.getMessage();
                return ERROR_STATUS;
            }
            try {
                Log.d("RVDebug", "Param URL" + param);
                //Parse Movie Image Information
                //If URL does not contain person path
                if (param.contains("images") && !param.contains("person")) {
                    resultString = downloadUrl(param);
                    parseMovieImages(resultString);
                }
            } catch (IOException e) {
                e.printStackTrace();
                ERROR_STRING = NETWORK_ERROR;
                ERROR_STATUS = e.getMessage();
                return ERROR_STATUS;
            }
            try {

                if (param.contains("person")) {
                    if (param.contains("popular")) {
                        //Parse Popular PersonInstance Information
                        resultString = downloadUrl(param);
                        parsePopularPersonInformation(resultString);
                    } else if (param.contains("images")) {
                        //Parse Person Images
                        resultString = downloadUrl(param);
                        parsePersonImages(resultString);
                    } else {
                        //Parse PersonInstance Information
                        resultString = downloadUrl(param);
                        parsePersonInformation(resultString);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                ERROR_STRING = NETWORK_ERROR;
                ERROR_STATUS = e.getMessage();
                return ERROR_STATUS;
            }
        }
        RESULT_STATUS = SUCCESS_STATUS;
        return SUCCESS_STATUS;
    }

    private void parseYTSRSSFeed(String resultString) {
        try {
            Log.d("torrentDebug", "Result String "+resultString);
            JSONObject resultObject = new JSONObject(resultString);
            mYTSMovieResultNotifier.onResultJSON(resultObject);
            JSONObject dataObject = resultObject.getJSONObject("data");
            JSONArray moviesArray = dataObject.getJSONArray("movies");
            int page_number = dataObject.getInt("page_number");

            mYTSMoviesArrayList = new ArrayList<>(moviesArray.length());


            for (int i = 0; i < moviesArray.length(); i++) {
                Log.d("torrentDebug", "\t"+i);
                YTSTorrent[] tA;
                JSONObject obj =
                        moviesArray.getJSONObject(i);
                JSONArray tJA = obj.getJSONArray("torrents");
                tA = new YTSTorrent[tJA.length()];
                for (int j = 0; j < tJA.length(); j++) {
                    JSONObject tObj = tJA.getJSONObject(j);
                    tA[j] = new YTSTorrent(
                            tObj.getString("url")
                            , tObj.getString("hash")
                            , tObj.getInt("seeds")
                            , tObj.getInt("peers")
                            , tObj.getString("size")
                            , tObj.getLong("size_bytes")
                            , tObj.getString("date_uploaded")
                            , tObj.getLong("date_uploaded_unix")
                    );
                }
                mYTSMoviesArrayList.add(
                        new YTSMovie(
                                obj.getLong("id")
                                , obj.getString("url")
                                , obj.getString("imdb_code")
                                , obj.getString("title")
                                , obj.getString("title_english")
                                , obj.getString("title_long")
                                , obj.getString("slug")
                                , obj.getInt("year")
                                , (float) obj.getDouble("rating")
                                , obj.getInt("runtime")
                                , obj.getString("genres")
                                , obj.getString("summary")
                                , obj.getString("description_full")
                                , obj.getString("synopsis")
                                , obj.getString("yt_trailer_code")
                                , page_number
                                , obj.getString("language")
                                , obj.getString("mpa_rating")
                                , obj.getString("background_image")
                                , obj.getString("background_image_original")
                                , obj.getString("small_cover_image")
                                , obj.getString("medium_cover_image")
                                , obj.getString("large_cover_image")
                                , tA
                                , obj.getString("date_uploaded_unix")

                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }
    }

    private void parseExtendedMovieData(String resultString) {
        try {
            JSONObject resultObject = new JSONObject(resultString);
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }

    }

    private void parsePopularPersonInformation(String resultString) {
        try {
            JSONObject resultObject = new JSONObject(resultString);
            long pageNumber = resultObject.getLong("page");
            JSONArray resultArray = resultObject.getJSONArray("results");
            ArrayList<MovieDataInstance> movieArrayList = new ArrayList<>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj = resultArray.getJSONObject(i);
                JSONArray knownForArray
                        = obj.getJSONArray("known_for");

                for (int j = 0; j < knownForArray.length(); j++) {
                    //TODO:Handle JSON Errors or find AnotherWay
                    movieArrayList.add(
                            null
                    );
                }
                mPopularPeopleArrayList.add(
                        new PopularPeopleInstance(
                                obj.getLong("id")
                                , obj.getDouble("popularity")
                                , movieArrayList
                                , obj.getString("profile_path")
                                , obj.getString("name")
                                , pageNumber
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }
    }

    private void parsePersonInformation(String jsonData) {
        try {
            JSONObject resultObject = new JSONObject(jsonData);
            ArrayList<String> alsoKnownAs = new ArrayList<>();
            JSONArray alsoKnownAsArray = resultObject.getJSONArray("also_known_as");
            for (int i = 0; i < alsoKnownAsArray.length(); i++) {
                alsoKnownAs.add(alsoKnownAsArray.getString(i));
            }
            mRequestedPersonData = new PersonInstance(
                    resultObject.getLong("id")
                    , resultObject.getDouble("popularity")
                    , resultObject.getBoolean("adult")
                    , resultObject.getString("biography")
                    , alsoKnownAs
                    , resultObject.getString("birthday")
                    , resultObject.getString("deathday")
                    , resultObject.getString("name")
                    , resultObject.getString("imdb_id")
                    , resultObject.getString("place_of_birth")
                    , resultObject.getString("profile_path")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }
    }

    private void parsePersonImages(String resultString) {
        try {

            mPersonImageList = new ArrayList<>();
            JSONObject resultObject = new JSONObject(resultString);
            mMovieSpecificResultNotifier.onResultJSON(resultObject);
            JSONArray profilesArray =
                    resultObject.getJSONArray("profiles");
            for (int i = 0; i < profilesArray.length(); i++) {
                JSONObject o = profilesArray.getJSONObject(i);
                mPersonImageList.add(
                        new PersonImage(
                                o.getString("iso_639_1")
                                , o.getString("file_path")
                                , o.getInt("height")
                                , o.getInt("width")
                                , o.getDouble("vote_average")
                                , o.getInt("vote_count")
                                , o.getInt("aspect_ratio")
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseMovieImages(String resultString) {
        try {
            mPosterList = new ArrayList<>();
            mBackDropList = new ArrayList<>();
            JSONObject resultObject = new JSONObject(resultString);

            mMovieSpecificResultNotifier.onResultJSON(resultObject);

            JSONArray backDropArray = resultObject.getJSONArray("backdrops");
            JSONArray posterArray = resultObject.getJSONArray("posters");

            for (int i = 0; i < backDropArray.length(); i++) {
                JSONObject backDrop = backDropArray.getJSONObject(i);
                mBackDropList.add(new MovieImage(
                        true
                        , backDrop.getString("iso_639_1")
                        , backDrop.getString("file_path")
                        , backDrop.getInt("height")
                        , backDrop.getInt("width")
                        , backDrop.getDouble("vote_average")
                        , backDrop.getInt("vote_count")
                ));

            }
            for (int i = 0; i < posterArray.length(); i++) {
                JSONObject poster = posterArray.getJSONObject(i);
                mPosterList.add(new MovieImage(
                        true
                        , poster.getString("iso_639_1")
                        , poster.getString("file_path")
                        , poster.getInt("height")
                        , poster.getInt("width")
                        , poster.getDouble("vote_average")
                        , poster.getInt("vote_count")
                ));

            }
            RESULT_STATUS = SUCCESS_STATUS;
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals(SUCCESS_STATUS) && ERROR_STRING == null) {
            if (mMovieSpecificResultNotifier != null) {
                Log.d("RVDebug", "onPostExecute");
                mMovieSpecificResultNotifier.onResultParsedIntoCastList(castDataArrayList);
                mMovieSpecificResultNotifier.onResultParsedIntoCrewList(crewDataArrayList);
                mMovieSpecificResultNotifier.onResultParsedIntoMovieImages(mBackDropList, mPosterList, mPersonImageList);
                Log.d("RVDebug", "Executing After calling method");
                mMovieSpecificResultNotifier.onResultParsedIntoPopularPersonInfo(mPopularPeopleArrayList);
                mMovieSpecificResultNotifier.onResultParsedIntoPersonData(mRequestedPersonData);
            } else {
                mYTSMovieResultNotifier.onMovieDataParsed(mYTSMoviesArrayList);
            }
        } else if (ERROR_STRING != null) {
            if (mYTSMovieResultNotifier != null)
                mYTSMovieResultNotifier.onResultString(resultString, ERROR_STRING, ERROR_STATUS);
            else
                mMovieSpecificResultNotifier.onResultString(resultString, ERROR_STRING, ERROR_STATUS);
        }
    }

    private void parseCastCrewCredits(String jsonData) {
        try {
            castDataArrayList = new ArrayList<>();
            crewDataArrayList = new ArrayList<>();
            contentValueCastCrewArrayList = new ArrayList<>();

            JSONObject resultObject = new JSONObject(jsonData);
            //Show JSON Result IN LOG
            mMovieSpecificResultNotifier.onResultJSON(resultObject);

            long MOVIE_ID = resultObject.getLong("id");
            JSONArray castJSONArray = resultObject.getJSONArray("cast");
            JSONArray crewJSONArray = resultObject.getJSONArray("crew");

            //Parse Cast Data
            for (int i = 0; i < castJSONArray.length(); i++) {
                JSONObject castJSONObject = castJSONArray.getJSONObject(i);
                CastDataInstance castInstance = new CastDataInstance(
                        MOVIE_ID
                        , castJSONObject.getString("name")
                        , castJSONObject.getString("credit_id")
                        , castJSONObject.getString("profile_path")
                        , castJSONObject.getInt("order")
                        , castJSONObject.getLong("id")
                        , castJSONObject.getLong("cast_id")
                        , castJSONObject.getString("character")
                );
                castDataArrayList.add(castInstance);
                contentValueCastCrewArrayList.add(getContentValueFromCastData(castInstance));
            }
            //Insert Cast Data
            try {
                insertCastDataIntoDataBase(contentValueCastCrewArrayList);
            } catch (SQLiteException e) {
                ERROR_STRING = SQLITE_CAST_INSERT_ERROR;
                ERROR_STATUS = e.getMessage();
                e.printStackTrace();
            }
            //Parse Crew Data
            //Create a new Array List for Crew Data ContentValues
            contentValueCastCrewArrayList = new ArrayList<>();

            for (int i = 0; i < crewJSONArray.length(); i++) {
                JSONObject crewJSONObject = crewJSONArray.getJSONObject(i);
                CrewDataInstance crewInstance = new CrewDataInstance(
                        MOVIE_ID
                        , crewJSONObject.getString("name")
                        , crewJSONObject.getString("credit_id")
                        , crewJSONObject.getLong("id")
                        , crewJSONObject.getString("profile_path")
                        , crewJSONObject.getString("department")
                        , crewJSONObject.getString("job")
                );
                crewDataArrayList.add(crewInstance);
                contentValueCastCrewArrayList.add(getContentValueFromCrewData(crewInstance));
            }
            try {
                insertCrewDataIntoDataBase(contentValueCastCrewArrayList);
            } catch (SQLiteException e) {
                ERROR_STRING = SQLITE_CREW_INSERT_ERROR;
                ERROR_STATUS = e.getMessage();
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ERROR_STRING = JSON_PARSE_ERROR;
        }
    }

    private ContentValues getContentValueFromCastData(CastDataInstance castInstance) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.CastData.COLUMN_MOVIE_ID, castInstance.getMOVIE_ID());
        values.put(MovieContract.CastData.COLUMN_CAST_ID, castInstance.getCAST_ID());
        values.put(MovieContract.CastData.COLUMN_CHARACTER, castInstance.getCHARACTER());
        values.put(MovieContract.CastData.COLUMN_CREDIT_ID, castInstance.getCREDIT_ID());
        values.put(MovieContract.CastData.COLUMN_UNKNOWN_ID, castInstance.getID());
        values.put(MovieContract.CastData.COLUMN_NAME, castInstance.getNAME());
        values.put(MovieContract.CastData.COLUMN_ORDER, castInstance.getORDER());
        values.put(MovieContract.CastData.COLUMN_PROFILE_PICTURE_PATH, castInstance.getPROFILE_PATH());
        return values;
    }

    private ContentValues getContentValueFromCrewData(CrewDataInstance crewInstance) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.CrewData.COLUMN_MOVIE_ID, crewInstance.getMOVIE_ID());
        values.put(MovieContract.CrewData.COLUMN_JOB, crewInstance.getJOB());
        values.put(MovieContract.CrewData.COLUMN_CREDIT_ID, crewInstance.getCREDIT_ID());
        values.put(MovieContract.CrewData.COLUMN_UNKNOWN_ID, crewInstance.getID());
        values.put(MovieContract.CrewData.COLUMN_NAME, crewInstance.getNAME());
        values.put(MovieContract.CrewData.COLUMN_PROFILE_PICTURE_PATH, crewInstance.getPROFILE_PATH());
        values.put(MovieContract.CrewData.COLUMN_DEPARTMENT, crewInstance.getDEPARTMENT());
        return values;
    }

    private void insertCastDataIntoDataBase(ArrayList<ContentValues> contentValueCastCrewArrayList) throws SQLiteException {
        int inserted = 0;
        ContentValues[] valueArray = new ContentValues[contentValueCastCrewArrayList.size()];
        contentValueCastCrewArrayList.toArray(valueArray);
        if (contentValueCastCrewArrayList.size() > 0) {
            inserted = mContext.getContentResolver()
                    .bulkInsert(
                            MovieContract.CastData.MOVIE_CAST_CONTENT_URI
                            , valueArray
                    );
            Log.e("Inserted : \t", String.valueOf(inserted));
        }
        if (inserted < 0) {
            ERROR_STRING = SQLITE_CAST_INSERT_ERROR;
        }
    }

    private void insertCrewDataIntoDataBase(ArrayList<ContentValues> contentValueCastCrewArrayList) throws SQLiteException {
        int inserted = 0;
        ContentValues[] valueArray = new ContentValues[contentValueCastCrewArrayList.size()];
        contentValueCastCrewArrayList.toArray(valueArray);
        if (contentValueCastCrewArrayList.size() > 0) {
            inserted = mContext.getContentResolver()
                    .bulkInsert(
                            MovieContract.CrewData.MOVIE_CREW_CONTENT_URI
                            , valueArray
                    );
            Log.e("Inserted : \t", String.valueOf(inserted));
        }
        if (inserted < 0) {
            ERROR_STRING = SQLITE_CREW_INSERT_ERROR;
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
