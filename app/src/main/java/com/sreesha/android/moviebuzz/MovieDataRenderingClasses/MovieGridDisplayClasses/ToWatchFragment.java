package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.R;

public class ToWatchFragment extends Fragment {
    public static final String TO_WATCH_WATCHED_PARAM_KEY = "toWatchWatchedParam";

    private final int TO_WATCH_LOADER_ID = MovieContract.UserFavourite.TO_WATCH_MOVIE_TYPE;
    private final int WATCHED_LOADER_ID = MovieContract.UserFavourite.WATCHED_MOVIE_TYPE;

    private int CURRENT_LOADER_ID = MovieContract.UserFavourite.FAVOURITE_MOVIE_TYPE;

    CoordinatorLayout mToWatchWatchedCoordinatorLayout;
    AppBarLayout mToWatchAppBarLayout;

    private String mParam1;
    private String mParam2;
    private static final String SCROLL_POSITION_VALUE_KEY = "scrollPositionKey";
    private boolean hasSortOrderChanged = false;
    private static final int LOADER_ID = 578;
    private static final int FAVOURITES_MOVIES_LOADER_ID = 579;


    RecyclerView mMovieDisplayRecyclerView;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    MovieRecyclerViewCursorAdapter mRecyclerViewCursorAdapter;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    int SPAN_COUNT = 2;

    float POSTER_WIDTH = 185f;
    private static final String MOVIE_LIST_PARCEL_KEY = "movieListParcelKey";
    private static final String MOVIE_TYPE_PARCEL_KEY = "movieTypeParcelKey";
    private static final String SORT_PREFERENCE_KEY = "sortPreferenceKey";

    boolean mIsStateRestored = false;
    Toolbar mToolBar;

    public ToWatchFragment() {

    }

    public static ToWatchFragment newInstance(int param) {
        ToWatchFragment mFragment = new ToWatchFragment();
        Bundle args = new Bundle();
        args.putInt(TO_WATCH_WATCHED_PARAM_KEY, param);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsStateRestored = true;
            CURRENT_LOADER_ID = savedInstanceState.getInt(TO_WATCH_WATCHED_PARAM_KEY);
        } else {
            if (getArguments() != null)
                CURRENT_LOADER_ID = getArguments().getInt(TO_WATCH_WATCHED_PARAM_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TO_WATCH_WATCHED_PARAM_KEY, CURRENT_LOADER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_watch_fragment, container, false);
        initializeViewElements(view);
        return view;
    }

    TextView mEmptyListMessageTV;

    void initializeViewElements(View view) {
        mToWatchAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        mToWatchWatchedCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.toWatchWatchedCoordinatorLayout);
        mMovieDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.movieDisplayRecyclerView);
        initializeRecyclerView();
        mToolBar = (Toolbar) view.findViewById(R.id.toolbar);
        mEmptyListMessageTV = (TextView) view.findViewById(R.id.emptyListMessageTV);

        switch (CURRENT_LOADER_ID) {
            case TO_WATCH_LOADER_ID:
                Log.d("Watched", "Loader ID - " + CURRENT_LOADER_ID);
                mToolBar.setTitle(R.string.to_watch_list_activity_title);
                break;
            case WATCHED_LOADER_ID:
                Log.d("Watched", "Loader ID - " + CURRENT_LOADER_ID);
                mToolBar.setTitle(R.string.watched_list_activity_title);
                break;
        }
    }

    private void initializeRecyclerView() {
             /*Get The current configuration of the display and get the orientation*/
        int orientation = getResources().getConfiguration().orientation;
        /*Check if the orientation is landscape or portrait and compute span count accordingly*/
        mRecyclerViewCursorAdapter = new MovieRecyclerViewCursorAdapter(getActivity(), null);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                SPAN_COUNT
                , LinearLayoutManager.VERTICAL
        );
        mStaggeredGridLayoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
        computerAndRegisterSpanCount();

        mMovieDisplayRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mMovieDisplayRecyclerView.setAdapter(mRecyclerViewCursorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsStateRestored) {
            Log.d("Watched", "State Restored - Restarting Loader");

        }
        getLoaderManager().destroyLoader(CURRENT_LOADER_ID);
        getLoaderManager().initLoader(CURRENT_LOADER_ID, null, loaderCallBacks);

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

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String sortOrder = null;
            Log.d("Watched", "onCreateLoader");
            switch (id) {
                case TO_WATCH_LOADER_ID:
                    Log.d("Watched", "ToWatchLoader");
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                            .buildUpon().appendPath(MovieContract.PATH_FAVOURITES_MOVIE_DATA).build()
                            , null
                            , MovieContract.UserFavourite.COLUMN_MOVIE_TYPE + " =?"
                            , new String[]{String.valueOf(MovieContract.UserFavourite.TO_WATCH_MOVIE_TYPE)}
                            , sortOrder
                    );
                case WATCHED_LOADER_ID:
                    Log.d("Watched", "WatchedLoader");
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                            .buildUpon().appendPath(MovieContract.PATH_FAVOURITES_MOVIE_DATA).build()
                            , null
                            , MovieContract.UserFavourite.COLUMN_MOVIE_TYPE + " =?"
                            , new String[]{String.valueOf(MovieContract.UserFavourite.WATCHED_MOVIE_TYPE)}
                            , sortOrder
                    );
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case TO_WATCH_LOADER_ID:
                    if (data.getCount() == 0) {
                        mEmptyListMessageTV.setVisibility(View.VISIBLE);
                        Log.d("Watched", "To Watch Loader - zero data count");
                    } else {
                        mEmptyListMessageTV.setVisibility(View.GONE);
                        Log.d("Watched", "To Watch Loader - Loading Data");
                        mRecyclerViewCursorAdapter.swapCursor(data);
                    }
                    break;
                case WATCHED_LOADER_ID:
                    if (data.getCount() == 0) {
                        mEmptyListMessageTV.setVisibility(View.VISIBLE);
                        Log.d("Watched", "Watched Loader - zero data count");
                    } else {
                        mEmptyListMessageTV.setVisibility(View.GONE);
                        Log.d("Watched", "Watched Loader - Loding Data");
                        mRecyclerViewCursorAdapter.swapCursor(data);
                    }
                    break;
                default:
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
            mRecyclerViewCursorAdapter.swapCursor(null);
        }
    };

    private void computerAndRegisterSpanCount() {
        if (MoviePosterGridActivity.isInTwoPaneMode()) {
            SPAN_COUNT = Math.round((convertPixelsToDP(
                    mToWatchWatchedCoordinatorLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        } else {
            SPAN_COUNT = (int) Math.round((convertPixelsToDP(
                    mToWatchWatchedCoordinatorLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        }
        if (SPAN_COUNT != 0 && mStaggeredGridLayoutManager != null)
            mStaggeredGridLayoutManager.setSpanCount(SPAN_COUNT);
    }

    /*Density-independent pixels is equal to one physical pixel on a 160dpi screen->Considered As the Baseline
                Therefore for a screen with 'x' dpi and 'px' pixels(say for width)
                :::
                "(x*px/160)" is the number in device independent pixels(i.e width in device independent pixels)*/
    private float convertPixelsToDP(int width) {
        return (width / /*screen Width in Device Independent pixels*/ (getResources().getDisplayMetrics().densityDpi / 160f));
    }
}
