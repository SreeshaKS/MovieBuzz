package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.AsyncSearchTask;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.EndlessRecyclerViewScrollChangeListener;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.EndlessRecyclerViewScrollListener;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MovieRecyclerViewCursorAdapter;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.OnMoreDataRequestedListener;
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


public class SimilarMoviesFragment extends Fragment implements OnMoreDataRequestedListener
        , AsyncSearchTask.SearchResultDispatchInterface
        , MovieTabsDetailFragment.MovieDetailedDataFragmentInterface {

    private static final String SCROLL_POSITION_VALUE_KEY = "scrollPositionKey";

    private static final int LOADER_ID = 578;

    Boolean isStateRestored = false;

    RecyclerView mMovieDisplayRecyclerView;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    int SPAN_COUNT = 2;
    String SORT_PREFERENCE = APIUrls.API_HIGHEST_RATING_DESC;
    float POSTER_WIDTH = 185f;
    private static final String MOVIE_LIST_PARCEL_KEY = "movieListParcelKey";

    ArrayList<MovieDataInstance> mMovieList = new ArrayList<>();
    private int[] mCurrentCompletelyVisibleItemPosition;
    private static Display currentDisplay;
    MovieRecyclerViewCursorAdapter mRecyclerViewCursorAdapter;

    CoordinatorLayout mPosterGridFragmentCoOrLayout;
    Uri.Builder URL = null;

    AsyncTask loadFreshDataTask = null, loadMoreDataTask = null;
    FrameLayout mPosterGridFragmentFrameLayout;
    CardView mEmptyFavouritesCardView;
    CardView mFavouritesRefreshCard;

    public static final String SIMILAR_MOVIE_PARCELABLE_KEY = "similarMoviesParcelableKey";
    private MovieDataInstance mMovieDataInstance;

    public SimilarMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, loaderCallBacks);
    }

    public static SimilarMoviesFragment newInstance(MovieDataInstance mMovieDataInstance) {
        SimilarMoviesFragment fragment = new SimilarMoviesFragment();
        Bundle args = new Bundle();
        args.putParcelable(SIMILAR_MOVIE_PARCELABLE_KEY, mMovieDataInstance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieDataInstance = getArguments().getParcelable(SIMILAR_MOVIE_PARCELABLE_KEY);
            if (mMovieDataInstance != null)
                URL = APIUrls.buildSimilarMoviesURL(String.valueOf(mMovieDataInstance.getMovieID()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similar_movies, container, false);

        mRecyclerViewCursorAdapter = new MovieRecyclerViewCursorAdapter(getActivity(), null, true);
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
            Log.e("Saved Instance", "Restoring Instances");

        } else {
            /*if the activity is starting for the first
            time and no saved instances are present
            then load data fom the server*/
                /*Instantiate View Elements*/
            initializeViewElements(view);
            /*Log.e("Saved Instance", "Loading Data Freshly");
            downloadFreshData();*/
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMovieDisplayRecyclerView != null)
            mMovieDisplayRecyclerView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (getActivity() != null) {
                                try {
                                    computerAndRegisterSpanCount();
                                } catch (IllegalStateException w) {
                                    w.printStackTrace();
                                }
                            }
                        }
                    });
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
                if (getActivity() != null && getActivity() instanceof MoviePosterGridActivity) {
                    Log.e("MovieTypes", stringObject);

                    ((MoviePosterGridActivity) getActivity())
                            .showNetworkConnectivityDialogue("Please connect to a working network connection");
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
                    getLoaderManager().restartLoader(LOADER_ID, null, loaderCallBacks);
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
        initializeRecyclerView();
        Log.e("URL", URL.build().toString());
    }

    private void restoreSavedInstances(Bundle savedInstanceState) {
        isStateRestored = true;
        mCurrentCompletelyVisibleItemPosition = savedInstanceState.getIntArray(SCROLL_POSITION_VALUE_KEY);
        mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCEL_KEY);
        initializeRecyclerView();
    }

    private void initializeViewElements(View view) {
        Log.e("OrientationDebug", "OrientationMightHaveChanged");

        mFavouritesRefreshCard = (CardView) view.findViewById(R.id.favouritesRefreshCard);

        mFavouritesRefreshCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("FavLoaderDebug", "LoaderRestart Dispatched");
                dispatchFavouritesReload();
            }
        });
        mEmptyFavouritesCardView = (CardView) view.findViewById(R.id.emptyFavouritesCard);
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
            SPAN_COUNT = (int) Math.ceil((convertPixelsToDP(
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
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            EndlessRecyclerViewScrollChangeListener.setLoadingToFalse();
        } else {
            EndlessRecyclerViewScrollListener.setLoadingToFalse();
        }
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LOADER_ID:
                    if (mMovieDataInstance != null) {
                        Log.e("SimilarMoviesLoader", "LoaderOnCreate");
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.MovieData.MOVIE_CONTENT_URI
                                , null
                                , MovieContract.MovieData.COLUMN_SIMILAR_TO_ID + " =?"
                                , new String[]{String.valueOf(mMovieDataInstance.getMovieID())}
                                , null
                        );
                    }
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            Log.e("SimilarMoviesLoader", "onLoadFinishedCalled Cursor has : " + data.getCount() + " items");
            if (mRecyclerViewCursorAdapter != null) {
                if (!(data.getCount() == 0)) {
                    mEmptyFavouritesCardView.setVisibility(View.GONE);
                    mMovieDisplayRecyclerView.setVisibility(View.VISIBLE);
                    mRecyclerViewCursorAdapter.swapCursor(data);
                } else {
                    downloadFreshData();
                }
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            Log.e("Debug", "onLoaderResetCalled");
            mRecyclerViewCursorAdapter.swapCursor(null);
        }
    };

    private void dispatchFavouritesReload() {
        getLoaderManager().destroyLoader(LOADER_ID);
        getLoaderManager().initLoader(LOADER_ID, null, loaderCallBacks);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

   /* @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        mCurrentCompletelyVisibleItemPosition = new int[SPAN_COUNT];
        mCurrentCompletelyVisibleItemPosition = mStaggeredGridLayoutManager
                .findFirstCompletelyVisibleItemPositions(
                        mCurrentCompletelyVisibleItemPosition
                );
    }*/

    @Override
    public void onSearchResultAcquired(MovieDataInstance instance) {

    }

    @Override
    public void OnMovieDataChanged(MovieDataInstance mMovieData) {
        if (mRecyclerViewCursorAdapter != null && mMovieDataInstance != null) {
            try {
                Log.e("CastCrewDebug", "OnMovieDataCHangedCalled");
                mMovieDataInstance = mMovieData;

                //getLoaderManager().destroyLoader(CAST_LOADER_ID);
                //getLoaderManager().initLoader(CAST_LOADER_ID, null, mLoaderCallBacks);
                Log.e("CastCrewDebug", "Calling Loader Manager");
            } catch (IllegalStateException e) {
                Log.e("CastCrewDebug", "Exception Inside OnMovieDataChanged : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
