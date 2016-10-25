package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieDataDBHelper;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncResult;
import com.sreesha.android.moviebuzz.Networking.DownloadData;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.Networking.PreferenceKeys;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MoviePosterGridFragment extends Fragment
        implements OnMoreDataRequestedListener

        , DispatchNavigationInterface
        , AsyncSearchTask.SearchResultDispatchInterface {

    private static final String SCROLL_POSITION_VALUE_KEY = "scrollPositionKey";
    private boolean hasSortOrderChanged = false;
    private static final int LOADER_ID = 578;
    private static final int FAVOURITES_MOVIES_LOADER_ID = 579;
    Boolean isStateRestored = false;
    boolean shouldShowOnlyFavourites = false;
    RecyclerView mMovieDisplayRecyclerView;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    int SPAN_COUNT = 2;
    String SORT_PREFERENCE = APIUrls.API_SORT_POPULARITY_DESC;
    float POSTER_WIDTH = 185f;
    private static final String MOVIE_LIST_PARCEL_KEY = "movieListParcelKey";
    private static final String MOVIE_TYPE_PARCEL_KEY = "movieTypeParcelKey";
    private static final String SORT_PREFERENCE_KEY = "sortPreferenceKey";
    ArrayList<MovieDataInstance> mMovieList = new ArrayList<>();
    private int[] mCurrentCompletelyVisibleItemPosition;
    private static Display currentDisplay;
    MovieRecyclerViewCursorAdapter mRecyclerViewCursorAdapter;
    SharedPreferences preferences;
    CoordinatorLayout mPosterGridFragmentCoOrLayout;
    DispatchNavigationInterface mNavigationDispatchListener;
    Uri.Builder URL = APIUrls.buildPopularMoviesURL()
            .appendQueryParameter(APIUrls.API_SORT_PARAM, SORT_PREFERENCE);
    MovieDisplayAdapter mMovieListDisplayAdapter;
    ArrayList<MovieDataInstance> searchResultMovieList = new ArrayList<>();

    AsyncTask loadFreshDataTask = null, loadMoreDataTask = null;


    MovieDataDBHelper mMovieDataDBHelper;
    SQLiteDatabase db;

    private enum MOVIE_TYPE {
        POPULAR_MOVIES, HIGHEST_RATED_MOVIES, UPCOMING_MOVIES, NOW_PLAYING_MOVIES, SEARCH_MOVIE_TYPE
    }

    MOVIE_TYPE mMovieType = MOVIE_TYPE.POPULAR_MOVIES;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("Debug", "OnResumeCalled,State Being Restored");
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        String sortOrder = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_options_list_key), getString(R.string.sort_options_list_preference_default_value));

        shouldShowOnlyFavourites = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getBoolean(getString(R.string.favourites_checkbox_preference_key), false);
        Log.e("FavouritesDebug", "Preference : " + shouldShowOnlyFavourites);
        setMovieTypeBasedOnSortOrder(sortOrder);
        if (sortOrder != null && !sortOrder.equals(SORT_PREFERENCE)) {
            hasSortOrderChanged = true;
            SORT_PREFERENCE = sortOrder;
            if (shouldShowOnlyFavourites) {
                getLoaderManager().restartLoader(FAVOURITES_MOVIES_LOADER_ID, null, loaderCallBacks);
            } else {
                initializeViewElementsAndDownloadFreshData();
                getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
            }
        } else if (sortOrder != null) {
            if (shouldShowOnlyFavourites) {
                getLoaderManager().restartLoader(FAVOURITES_MOVIES_LOADER_ID, null, loaderCallBacks);
            } else {
                getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
            }
        }

    }

    private void setMovieTypeBasedOnSortOrder(String sortOrder) {
        if (sortOrder.equals("vote_average.desc")) {
            mMovieType = MOVIE_TYPE.HIGHEST_RATED_MOVIES;
        } else {
            mMovieType = MOVIE_TYPE.POPULAR_MOVIES;
        }
    }

    public static MoviePosterGridFragment newInstance() {
        return new MoviePosterGridFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scrolling, container, false);
        setHasOptionsMenu(true);
        Log.e("Debug", "OnCreateViewCalled");

        mRecyclerViewCursorAdapter = new MovieRecyclerViewCursorAdapter(getActivity(), null);
        mRecyclerViewCursorAdapter.setOnMoreDataRequestedListener(this);
        /*Set the Default Values from the R.xml.preferences file
         *if this method has never been called before.
         * If the Method has been called before then skip the default value initialization.*/
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        /*Update The Sort Preference , maybe the preference has changed*/
        SORT_PREFERENCE = preferences
                .getString(
                        getString(R.string.sort_options_list_key
                                , getString(R.string.sort_options_list_preference_default_value)
                                /*Default Value For Preference*/
                        )
                        , "sort_preference"/*Default Value For Key*/
                );
        /*Get the current display configuration
        * so that the SPAN_COUNT variable for the Staggered Grid layout
        * can be update dynamically based on the orientation of the screen
        * and the screen dpi*/
        currentDisplay = ((WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        if (savedInstanceState != null) {
            initializeViewElements(view);
            restoreSavedInstances(savedInstanceState);
            Log.e("Saved Instance", "Restoring Instances");

        } else {
            /*if the activity is starting for the first
            time and no saved instances are present
            then load data fom the server*/
                /*Instantiate View Elements*/
            initializeViewElements(view);
            Log.e("Saved Instance", "Loading Data Freshly");
            initializeViewElementsAndDownloadFreshData();
        }
        mMovieDisplayRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (getActivity() != null) {
                            computeAndRegisterSpanCount();
                        }

                    }
                });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("Debug", "OnCreateCalled,Setting Default Value For Preferences");
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        Log.e("Debug", "OnCreateCalled,Setting Default Value For Preferences"
                + PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.sort_options_list_key)
                        , getString(R.string.sort_options_list_preference_default_value))
        );
        mMovieDataDBHelper = new MovieDataDBHelper(getContext());
        db = mMovieDataDBHelper.getWritableDatabase();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e("Debug", "onActivityCreatedCalled");
        getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
        super.onActivityCreated(savedInstanceState);
    }

    private void restoreSavedInstances(Bundle savedInstanceState) {
        isStateRestored = true;
        mCurrentCompletelyVisibleItemPosition = savedInstanceState.getIntArray(SCROLL_POSITION_VALUE_KEY);
        mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCEL_KEY);
        initializeRecyclerView();
    }

    private void initializeViewElementsAndDownloadFreshData() {
            /*Loading Data , Show spinner notification*/
        mSwipeToRefreshLayout.setEnabled(true);
        mSwipeToRefreshLayout.setRefreshing(true);
            /*Start AsyncTask to Query the server*/

        Log.e("MovieTypes", "Downoading Data");
        loadFreshDataTask = new DownloadData(new AsyncResult() {
            @Override
            public void onResultJSON(JSONObject object) throws JSONException {
                Log.e("MovieTypes", object.toString());
            }

            @Override
            public void onResultString(String stringObject) {
                if (getActivity() != null) {
                    Log.e("MovieTypes", stringObject);
                    ((MoviePosterGridActivity) getActivity())
                            .showNetworkConnectivityDialogue("Please connect to a working network connection");
                    PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putBoolean(PreferenceKeys.IS_DATA_ALREADY_LOADED, false)
                            .commit();
                }
            }

            @Override
            public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {
                if (getActivity() != null) {
                    Log.e("Parsed", "ParseDone");
                    Log.e("MovieTypes", "ParseDone");
                    mSwipeToRefreshLayout.setRefreshing(false);
                    /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
                     * Therefore disable SwipeToRefreshLayout And enable it when required*/
                    switch (mMovieType) {
                        case UPCOMING_MOVIES:
                        case NOW_PLAYING_MOVIES:
                            Log.e("MovieTypes", "AcquiredNew List : " + movieList.size());
                            MoviePosterGridFragment.this.mMovieList = new ArrayList<MovieDataInstance>();
                            MoviePosterGridFragment.this.mMovieList = movieList;
                            initializeRecyclerView();
                            break;
                        default:
                            Log.e("MovieTypes", "Normal Movies Showing");
                            initializeRecyclerView();
                            getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
                    /*Update preferences , Data Has Been Downloaded
             , Data can be loaded from Content Provider
                    on Next Application boot or on next configurationChange*/
                            PreferenceManager
                                    .getDefaultSharedPreferences(getActivity())
                                    .edit()
                                    .putBoolean(PreferenceKeys.IS_DATA_ALREADY_LOADED, true)
                                    .commit();
                    }
                }
            }

            @Override
            public void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList) {

            }

            @Override
            public void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList) {

            }
        }
                , getActivity()
        ).execute(URL.build().toString());
        //Initialize RecyclerView And Load Data from Content Provider
        initializeRecyclerView();
        Log.e("URL", URL.build().toString());
    }

    private void initializeViewElements(View view) {
        Log.e("OrientationDebug", "OrientationMightHaveChanged");
        mMovieDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.movieDisplayRecyclerView);

        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.movieSwipeToRefreshLayout);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hasSortOrderChanged = true;
                //deleteAllMovieData();
                initializeViewElementsAndDownloadFreshData();
            }
        });
        /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
        * ThereforeDisable SwipeToRefreshLayout And enable it when required*/
        mSwipeToRefreshLayout.setRefreshing(false);
        mPosterGridFragmentCoOrLayout = (CoordinatorLayout) view.findViewById(R.id.posterGridCoordinatorLayout);
    }

    void deleteAllMovieData() {
        if (db.isOpen()) {
            db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA, null
                    , null);
        } else {
            db = mMovieDataDBHelper.getWritableDatabase();
            db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA, null
                    , null);
        }
    }

    private void initializeRecyclerView() {
             /*Get The current configuration of the display and get the orientation*/
        int orientation = getResources().getConfiguration().orientation;
        /*Check if the orientation is landscape or portrait and compute span count accordingly*/
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                SPAN_COUNT
                , LinearLayoutManager.VERTICAL
        );
        mStaggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        //computeAndRegisterSpanCount();
        mMovieDisplayRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {

            mMovieDisplayRecyclerView.setOnScrollChangeListener(new EndlessRecyclerViewScrollChangeListener(
                    mStaggeredGridLayoutManager, this, mMovieDisplayRecyclerView
            ));
        } else {
            mMovieDisplayRecyclerView.setOnScrollListener(new EndlessRecyclerViewScrollListener(
                    mStaggeredGridLayoutManager, this, mMovieDisplayRecyclerView
            ));
        }
        switch (mMovieType) {
            case UPCOMING_MOVIES:
            case NOW_PLAYING_MOVIES:
                Log.e("MovieTypes", mMovieType.toString());
                if (mMovieListDisplayAdapter == null) {
                    Log.e("MovieTypes", "AdapterNull");
                    mMovieListDisplayAdapter = new MovieDisplayAdapter(mMovieList, getActivity());
                    mMovieDisplayRecyclerView.setAdapter(mMovieListDisplayAdapter);
                    mMovieDisplayRecyclerView.invalidate();
                } else {
                    if (!(mMovieDisplayRecyclerView.getAdapter() instanceof MovieDisplayAdapter)) {
                        Log.e("MovieTypes", "AdapterNOTNull");
                        mMovieDisplayRecyclerView.setAdapter(mMovieListDisplayAdapter);
                        mMovieListDisplayAdapter.notifyDataSetChanged();
                        mMovieDisplayRecyclerView.invalidate();
                    } else {
                        mMovieListDisplayAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Log.e("MovieTypes", mMovieType.toString());
                mMovieDisplayRecyclerView.setAdapter(mRecyclerViewCursorAdapter);
        }
        if (isStateRestored && mCurrentCompletelyVisibleItemPosition != null) {
            Log.e("Debug", "RecyclerViewInitialized,State Being Restored");
            restoreRecyclerViewPosition();
        }
    }

    public void restoreRecyclerViewPosition() {
        if (mCurrentCompletelyVisibleItemPosition != null) {
            try {
                mMovieDisplayRecyclerView.smoothScrollToPosition(mCurrentCompletelyVisibleItemPosition[0]);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
                try {
                    mMovieDisplayRecyclerView.smoothScrollToPosition(mCurrentCompletelyVisibleItemPosition[1]);
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private void computeAndRegisterSpanCount() {
        Log.e("Span Debug", "Screen Width : " + convertPixelsToDP(
                mPosterGridFragmentCoOrLayout.getWidth()) + "Computer Value" + (convertPixelsToDP(
                mPosterGridFragmentCoOrLayout.getWidth())
                + convertPixelsToDP(
                (int) getResources()
                        .getDimension(R.dimen.movie_poster_margin)
        )
        ));
        if (MoviePosterGridActivity.isInTwoPaneMode()) {
            SPAN_COUNT = Math.round((convertPixelsToDP(
                    mPosterGridFragmentCoOrLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        } else {
            SPAN_COUNT = (int) Math.round((convertPixelsToDP(
                    mPosterGridFragmentCoOrLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
            Log.d("SpanCount", "Count : " + SPAN_COUNT
                    + "\n\t\tLayout Width : " + convertPixelsToDP(
                    mPosterGridFragmentCoOrLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            ) + "\n" +
                    "\t\tPoster Width " + POSTER_WIDTH);
        }
        if (SPAN_COUNT != 0 && mStaggeredGridLayoutManager != null)
            mStaggeredGridLayoutManager.setSpanCount(SPAN_COUNT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCEL_KEY, mMovieList);
        outState.putIntArray(SCROLL_POSITION_VALUE_KEY, mCurrentCompletelyVisibleItemPosition);
        outState.putString(SORT_PREFERENCE_KEY, SORT_PREFERENCE);
        outState.putSerializable(MOVIE_TYPE_PARCEL_KEY, mMovieType);
    }


    /*Density-independent pixels is equal to one physical pixel on a 160dpi screen->Considered As the Baseline
                Therefore for a screen with 'x' dpi and 'px' pixels(say for width)
                :::
                "(x*px/160)" is the number in device independent pixels(i.e width in device independent pixels)*/
    private float convertPixelsToDP(int width) {
        return (width / /*screen Width in Device Independent pixels*/ (getResources().getDisplayMetrics().densityDpi / 160f));
    }


    @Override
    public void onMoreDataRequested(int currentPage) {

        /*Start AsyncTask to Query the server*//*
        if (!mRecyclerViewCursorAdapter.mIsMoreDataLoading && !shouldShowOnlyFavourites) {*/
            /*Loading Data , Show spinner notification*/
        mSwipeToRefreshLayout.setEnabled(true);
        mSwipeToRefreshLayout.setRefreshing(true);
        Log.e("Loading Page : ", "" + currentPage);
            /*Prevent FurtherCallBacks from onBindViewHolder from spawning multiple Async Tasks
            * Setting mIsMoreDataLoading=true , ensures the code waits for a particular page to load
            * before new page is requested*/
            /*mRecyclerViewCursorAdapter.mIsMoreDataLoading = true;
            Log.e("SemaphoreDebug","IsMoreDataLoading ? "+mRecyclerViewCursorAdapter.mIsMoreDataLoading);*/
        //Update the current loading page
        loadMoreDataTask = new DownloadData(new AsyncResult() {
            @Override
            public void onResultJSON(JSONObject object) throws JSONException {
            }

            @Override
            public void onResultString(String stringObject) {
                if (getActivity() != null) {
                    ((MoviePosterGridActivity) getActivity())
                            .showNetworkConnectivityDialogue("Please Connect to a working Network");
                }
            }

            @Override
            public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {
                if (getActivity() != null) {
                    switch (mMovieType) {
                        case UPCOMING_MOVIES:
                        case NOW_PLAYING_MOVIES:
                            Log.e("MovieTypesDebug", "Movie Type : " + mMovieType.toString());
                            for (MovieDataInstance instance : movieList
                                    ) {
                                MoviePosterGridFragment.this.mMovieList.add(instance);
                            }
                            if (mMovieList != null) {
                                if (mMovieListDisplayAdapter != null) {
                                    mMovieListDisplayAdapter.setMovieList(mMovieList);
                                    mMovieListDisplayAdapter.notifyDataSetChanged();
                                } else {
                                    mMovieListDisplayAdapter = new MovieDisplayAdapter(mMovieList, getActivity());
                                    mMovieDisplayRecyclerView.setAdapter(mMovieListDisplayAdapter);
                                }
                                mSwipeToRefreshLayout.setRefreshing(false);
                                EndlessRecyclerViewScrollChangeListener.setLoadingToFalse();
                            }

                            break;
                        default:
                            /*Load Done, Dismiss The refresh spinner*/
                            getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);

                    }
                }
            }

            @Override
            public void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList) {

            }

            @Override
            public void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList) {

            }
        }
                , getActivity()
        ).execute(
                URL.appendQueryParameter(APIUrls.API_PAGE_PARAM, String.format("%d", (currentPage + 1)))
                        .build()
                        .toString()
        );
        Log.e("Movie URL", URL.appendQueryParameter(APIUrls.API_PAGE_PARAM, String.format("%d", (currentPage + 1)))
                .build()
                .toString());
    }

    @Override
    public void OnNavigationItemClicked(int menuItemID) {
        switch (menuItemID) {
            case R.id.highestRatedMoviesNavMenu:
                if (!SORT_PREFERENCE.equals("vote_average.desc")) {
                    mMovieType = MOVIE_TYPE.HIGHEST_RATED_MOVIES;
                    hasSortOrderChanged = true;
                    SORT_PREFERENCE = "vote_average.desc";
                    URL = APIUrls.buildPopularMoviesURL()
                            .appendQueryParameter(APIUrls.API_SORT_PARAM, SORT_PREFERENCE);
                    initializeViewElementsAndDownloadFreshData();
                    //TODO:UPDATE UI AND DISPLAY SORT PREFERENCE

                }
                mMovieListDisplayAdapter = null;
                setActivityTitle("Highest Rated Movies");
                break;
            case R.id.mostPopularMoviesNavMenu:
                mMovieType = MOVIE_TYPE.POPULAR_MOVIES;
                if (!SORT_PREFERENCE.equals("popularity.desc")) {
                    hasSortOrderChanged = true;
                    SORT_PREFERENCE = "popularity.desc";
                    URL = APIUrls.buildPopularMoviesURL()
                            .appendQueryParameter(APIUrls.API_SORT_PARAM, SORT_PREFERENCE);
                    initializeViewElementsAndDownloadFreshData();
                    //TODO:UPDATE UI AND DISPLAY SORT PREFERENCE
                }
                mMovieListDisplayAdapter = null;
                setActivityTitle("Popular Movies");
                break;
            case R.id.upComingMoviesNavMenu:
                mMovieType = MOVIE_TYPE.UPCOMING_MOVIES;
                mMovieList.clear();
                hasSortOrderChanged = true;
                URL = APIUrls.buildUpcomingMoviesURL();
                Toast.makeText(getActivity(), "upComingMovies", Toast.LENGTH_SHORT).show();
                initializeViewElementsAndDownloadFreshData();
                //TODO:UPDATE UI AND DISPLAY SORT PREFERENCE
                setActivityTitle("Upcoming Movies");
                mMovieListDisplayAdapter = null;
                if (searchTask != null) {
                    searchTask.cancelSearchProcess();
                    searchTask = null;
                }
                break;
            case R.id.nowPlayingMoviesNavMenu:
                mMovieType = MOVIE_TYPE.NOW_PLAYING_MOVIES;
                mMovieList.clear();
                hasSortOrderChanged = true;
                URL = APIUrls.buildPlayingNowMoviesURL();
                Toast.makeText(getActivity(), "nowPlayingMovies", Toast.LENGTH_SHORT).show();
                initializeViewElementsAndDownloadFreshData();
                //TODO:UPDATE UI AND DISPLAY SORT PREFERENCE
                setActivityTitle("Movies Playing Now");
                mMovieListDisplayAdapter = null;
                if (searchTask != null) {
                    searchTask.cancelSearchProcess();
                    searchTask = null;
                }
                break;
        }
    }

    private void setActivityTitle(CharSequence title) {
        if (getActivity() != null)
            getActivity().setTitle(title);
    }

    public interface NotifyMovieClick {
        void onMovieClicked(ArrayList<MovieDataInstance> parcelableMovieList, MovieDataInstance instance);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void dispatchQueryResult(Cursor queryResultCursor, String queryMovieTitleString) {
        if (queryMovieTitleString != null || queryResultCursor != null) {
            switch (mMovieType) {
                case POPULAR_MOVIES:
                case HIGHEST_RATED_MOVIES:
                    if (mRecyclerViewCursorAdapter != null && queryResultCursor != null) {
                        mRecyclerViewCursorAdapter.swapCursor(queryResultCursor);
                    } else {
                        getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
                    }
                    break;
            }
        } else {
            if (searchTask != null) {
                searchTask.cancelSearchProcess();
            }
        }
    }

    AsyncSearchTask searchTask;

    @Override
    public void onSearchResultAcquired(MovieDataInstance instance) {
        if (searchResultMovieList != null) {
            if (searchResultMovieList.size() != 0) {
                searchResultMovieList.add(instance);
                mMovieListDisplayAdapter.notifyDataSetChanged();
            } else {
                mMovieListDisplayAdapter.setMovieList(searchResultMovieList);
                mMovieListDisplayAdapter.notifyDataSetChanged();
            }
        } else {
            searchResultMovieList = new ArrayList<>();
            searchResultMovieList.add(instance);
            mMovieListDisplayAdapter.setMovieList(searchResultMovieList);
            mMovieListDisplayAdapter.notifyDataSetChanged();
        }
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder;
            if (SORT_PREFERENCE.equals("vote_average.desc")) {
                sortOrder = MovieContract.MovieData.COLUMN_VOTE_AVERAGE + " DESC";
            } else {
                sortOrder = MovieContract.MovieData.COLUMN_POPULARITY + " DESC";
            }
            switch (id) {
                case LOADER_ID:
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.MovieData.MOVIE_CONTENT_URI
                            , null
                            , MovieContract.MovieData.COLUMN_MOVIE_TYPE + " = ?"
                            , new String[]{String.valueOf(MovieContract.MovieData.POPULAR_MOVIE_TYPE)}
                            , null
                    );
                case FAVOURITES_MOVIES_LOADER_ID:
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                            .buildUpon().appendPath(MovieContract.PATH_FAVOURITES_MOVIE_DATA).build()
                            , null
                            , null
                            , null
                            , sortOrder
                    );
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            if (mRecyclerViewCursorAdapter != null) {
                switch (loader.getId()) {
                    case LOADER_ID:
                        switch (mMovieType) {
                            case UPCOMING_MOVIES:
                            case NOW_PLAYING_MOVIES:
                                mMovieListDisplayAdapter.notifyDataSetChanged();
                                mSwipeToRefreshLayout.setRefreshing(false);

                                break;
                            default:
                                mRecyclerViewCursorAdapter.swapCursor(data);
                                mSwipeToRefreshLayout.setRefreshing(false);

                        }
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                            EndlessRecyclerViewScrollChangeListener.setLoadingToFalse();
                        } else {
                            EndlessRecyclerViewScrollListener.setLoadingToFalse();
                        }
                        break;
                    case FAVOURITES_MOVIES_LOADER_ID:
                        if (loader.getId() == FAVOURITES_MOVIES_LOADER_ID) {
                            if (data.getCount() == 0) {
                            /*TODO:UPDATE UI FOR NO FAVOURITES SELECTED*/
                            }
                        }
                        break;
                }
                mRecyclerViewCursorAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            mRecyclerViewCursorAdapter.swapCursor(null);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (loadFreshDataTask != null)
            if (!loadFreshDataTask.isCancelled()) {
                loadFreshDataTask.cancel(true);
                loadFreshDataTask = null;
            }
        if (loadMoreDataTask != null)
            if (!loadMoreDataTask.isCancelled()) {
                loadMoreDataTask.cancel(true);
                loadMoreDataTask = null;
            }
        if (db.isOpen()) {
            db.close();
        }
    }
}
