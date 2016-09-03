package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncResult;
import com.sreesha.android.moviebuzz.Networking.DownloadData;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.Networking.PreferenceKeys;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HighestRatedMoviesFragment extends Fragment implements OnMoreDataRequestedListener
        , AsyncSearchTask.SearchResultDispatchInterface {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
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
    String SORT_PREFERENCE = APIUrls.API_HIGHEST_RATING_DESC;
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
    FrameLayout mPosterGridFragmentFrameLayout;

    OnScrollListener mRecyclerViewScrollListener;

    public HighestRatedMoviesFragment() {
        // Required empty public constructor
    }


    public static HighestRatedMoviesFragment newInstance(String param1, String param2) {
        HighestRatedMoviesFragment fragment = new HighestRatedMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highest_rated_movies, container, false);

        mRecyclerViewCursorAdapter = new MovieRecyclerViewCursorAdapter(getActivity(), null);
        mRecyclerViewCursorAdapter.setOnMoreDataRequestedListener(this);

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
        } else {
            /*if the activity is starting for the first
            time and no saved instances are present
            then load data fom the server*/
                /*Instantiate View Elements*/
            initializeViewElements(view);
            downloadFreshData();
        }
        return view;
    }

    private void downloadFreshData() {
            /*Loading Data , Show spinner notification*/
        mSwipeToRefreshLayout.setEnabled(true);
        mSwipeToRefreshLayout.setRefreshing(true);
            /*Start AsyncTask to Query the server*/
        mSwipeToRefreshLayout.setRefreshing(true);
        loadFreshDataTask = new DownloadData(new AsyncResult() {
            @Override
            public void onResultJSON(JSONObject object) throws JSONException {
            }

            @Override
            public void onResultString(String stringObject) {
                ((MoviePosterGridActivity) getActivity())
                        .showNetworkConnectivityDialogue("Please connect to a working network connection");
                PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putBoolean(PreferenceKeys.IS_DATA_ALREADY_LOADED, false)
                        .commit();
            }

            @Override
            public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {
                mSwipeToRefreshLayout.setRefreshing(false);
                    /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
                     * Therefore disable SwipeToRefreshLayout And enable it when required*/
                mSwipeToRefreshLayout.setEnabled(false);
                getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
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
        initializeRecyclerView();
    }

    private void restoreSavedInstances(Bundle savedInstanceState) {
        isStateRestored = true;
        mCurrentCompletelyVisibleItemPosition = savedInstanceState.getIntArray(SCROLL_POSITION_VALUE_KEY);
        mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCEL_KEY);
        initializeRecyclerView();
    }

    private void initializeViewElements(View view) {
        mPosterGridFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.moviePosterFragmentFrameLayout);
        mMovieDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.movieDisplayRecyclerView);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.movieSwipeToRefreshLayout);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                downloadFreshData();
            }
        });
        /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
        * ThereforeDisable SwipeToRefreshLayout And enable it when required*/
        mSwipeToRefreshLayout.setRefreshing(false);
        mPosterGridFragmentCoOrLayout = (CoordinatorLayout) view.findViewById(R.id.posterGridCoordinatorLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMovieDisplayRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (getActivity() != null) {
                            computerAndRegisterSpanCount();
                        }
                    }
                });
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
        computerAndRegisterSpanCount();
        mMovieDisplayRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mMovieDisplayRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        computerAndRegisterSpanCount();
                    }
                });
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            mMovieDisplayRecyclerView.setOnScrollChangeListener(new EndlessRecyclerViewScrollChangeListener(
                    mStaggeredGridLayoutManager, this, mMovieDisplayRecyclerView
            ));
        } else {
            mMovieDisplayRecyclerView.setOnScrollListener(new EndlessRecyclerViewScrollListener(
                    mStaggeredGridLayoutManager, this, mMovieDisplayRecyclerView
            ));
        }

        mMovieDisplayRecyclerView.setAdapter(mRecyclerViewCursorAdapter);
        if (isStateRestored && mCurrentCompletelyVisibleItemPosition != null) {
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

    private void computerAndRegisterSpanCount() {
        if (MoviePosterGridActivity.isInTwoPaneMode()) {
            SPAN_COUNT = Math.round((convertPixelsToDP(
                    mPosterGridFragmentFrameLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        } else {
            SPAN_COUNT = (int) Math.round((convertPixelsToDP(
                    mPosterGridFragmentFrameLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        }
        if (SPAN_COUNT != 0 && mStaggeredGridLayoutManager != null)
            mStaggeredGridLayoutManager.setSpanCount(SPAN_COUNT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCEL_KEY, mMovieList);
        outState.putIntArray(SCROLL_POSITION_VALUE_KEY, mCurrentCompletelyVisibleItemPosition);
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

                    for (MovieDataInstance instance : movieList
                            ) {
                        HighestRatedMoviesFragment.this.mMovieList.add(instance);
                    }

                    mSwipeToRefreshLayout.setRefreshing(false);
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                        EndlessRecyclerViewScrollChangeListener.setLoadingToFalse();
                    } else {
                        EndlessRecyclerViewScrollListener.setLoadingToFalse();
                    }
                    /*getLoaderManager().destroyLoader(LOADER_ID);
                    getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);*/
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
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.e("Debug", "onCreateLoaderCalled");
            switch (id) {
                case LOADER_ID:
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.MovieData.MOVIE_CONTENT_URI
                            , null
                            , MovieContract.MovieData.COLUMN_MOVIE_TYPE + " = ?"
                            , new String[]{String.valueOf(MovieContract.MovieData.HIGHEST_RATED_MOVIE_TYPE)}
                            , null
                    );
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            Log.e("Debug", "onLoadFinishedCalled Cursor has : " + data.getCount() + " items");
            if (mRecyclerViewCursorAdapter != null) {
                mRecyclerViewCursorAdapter.swapCursor(data);
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    EndlessRecyclerViewScrollChangeListener.setLoadingToFalse();
                } else {
                    EndlessRecyclerViewScrollListener.setLoadingToFalse();
                }
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            Log.e("Debug", "onLoaderResetCalled");
            mRecyclerViewCursorAdapter.swapCursor(null);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onSearchResultAcquired(MovieDataInstance instance) {

    }
}
