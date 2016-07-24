package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncResult;
import com.sreesha.android.moviebuzz.Networking.DownloadData;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayingNowMoviesFragment extends Fragment
        implements
        OnMoreDataRequestedListener
        , AsyncSearchTask.SearchResultDispatchInterface {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private static final String SCROLL_POSITION_VALUE_KEY = "scrollPositionKey";

    Boolean isStateRestored = false;
    RecyclerView mMovieDisplayRecyclerView;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    int SPAN_COUNT = 2;

    float POSTER_WIDTH = 185f;
    private static final String MOVIE_LIST_PARCEL_KEY = "movieListParcelKey";
    ArrayList<MovieDataInstance> mMovieList = new ArrayList<>();
    private int[] mCurrentCompletelyVisibleItemPosition;
    SharedPreferences preferences;
    MovieDisplayAdapter mMovieListDisplayAdapter;
    ArrayList<MovieDataInstance> searchResultMovieList = new ArrayList<>();

    Uri.Builder URL = APIUrls.buildPlayingNowMoviesURL();
    FrameLayout mPosterGridFragmentFrameLayout;
    AsyncTask loadFreshDataTask = null, loadMoreDataTask = null;

    public PlayingNowMoviesFragment() {
        // Required empty public constructor
    }

    public static PlayingNowMoviesFragment newInstance(String param1, String param2) {
        PlayingNowMoviesFragment fragment = new PlayingNowMoviesFragment();
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
        View view = inflater.inflate(R.layout.fragment_playing_now_movies, container, false);
        if (savedInstanceState != null) {
            initializeViewElements(view);
            restoreSavedInstances(savedInstanceState);
            Log.e("Saved Instance", "Restoring Instances");

        } else {
            initializeViewElements(view);
            Log.e("Saved Instance", "Loading Data Freshly");
            downloadFreshData();
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCEL_KEY, mMovieList);
        outState.putIntArray(SCROLL_POSITION_VALUE_KEY, mCurrentCompletelyVisibleItemPosition);
    }

    private void initializeViewElements(View view) {
        Log.e("OrientationDebug", "OrientationMightHaveChanged");
        mPosterGridFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.moviePosterFragmentFrameLayout);
        mMovieDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.movieDisplayRecyclerView);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.movieSwipeToRefreshLayout);
        mSwipeToRefreshLayout.setEnabled(true);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeToRefreshLayout.isRefreshing())
                    mSwipeToRefreshLayout.setRefreshing(false);
                downloadFreshData();
            }
        });
        /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
        * ThereforeDisable SwipeToRefreshLayout And enable it when required*/
        mSwipeToRefreshLayout.setRefreshing(false);
    }

    private void restoreSavedInstances(Bundle savedInstanceState) {
        isStateRestored = true;
        mCurrentCompletelyVisibleItemPosition = savedInstanceState.getIntArray(SCROLL_POSITION_VALUE_KEY);
        mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCEL_KEY);
        initializeRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMovieDisplayRecyclerView != null) {
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

        mMovieListDisplayAdapter = new MovieDisplayAdapter(mMovieList, getActivity());

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
        mMovieDisplayRecyclerView.setAdapter(mMovieListDisplayAdapter);
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

    private void downloadFreshData() {
            /*Loading Data , Show spinner notification*/
        mSwipeToRefreshLayout.setEnabled(true);
        mSwipeToRefreshLayout.setRefreshing(true);
            /*Start AsyncTask to Query the server*/
        mSwipeToRefreshLayout.setRefreshing(true);


        loadFreshDataTask = new DownloadData(new AsyncResult() {
            @Override
            public void onResultJSON(JSONObject object) throws JSONException {
                Log.e("MovieTypes", object.toString());
            }

            @Override
            public void onResultString(String stringObject) {
                Log.e("MovieTypes", stringObject);
            }

            @Override
            public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {
                if (getActivity() != null) {
                    Log.e("Parsed", "ParseDone");
                    Log.e("MovieTypes", "ParseDone");
                    mSwipeToRefreshLayout.setRefreshing(false);
                    /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
                     * Therefore disable SwipeToRefreshLayout And enable it when required*/
                    PlayingNowMoviesFragment.this.mMovieList = new ArrayList<MovieDataInstance>();
                    PlayingNowMoviesFragment.this.mMovieList = movieList;
                    initializeRecyclerView();
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

        Log.e("URL", URL.build().toString());
    }

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

    private void computerAndRegisterSpanCount() {
        Log.e("Span Debug", "Screen Width : " + convertPixelsToDP(
                mPosterGridFragmentFrameLayout.getWidth()) + "Computer Value" + (convertPixelsToDP(
                mPosterGridFragmentFrameLayout.getWidth())
                + convertPixelsToDP(
                (int) getResources()
                        .getDimension(R.dimen.movie_poster_margin)
        )
        ));
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
        if (SPAN_COUNT != 0&&mStaggeredGridLayoutManager!=null)
            mStaggeredGridLayoutManager.setSpanCount(SPAN_COUNT);
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
                        PlayingNowMoviesFragment.this.mMovieList.add(instance);
                    }
                    mSwipeToRefreshLayout.setRefreshing(false);
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
    }
}
