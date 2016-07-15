package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieDataDBHelper;
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
import java.util.List;

/**
 * Created by Sreesha on 16-05-2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class BottomSheetSearchReveal extends BottomSheetDialogFragment implements OnMoreDataRequestedListener
        , RecyclerView.OnScrollChangeListener
        , AsyncSearchTask.SearchResultDispatchInterface {
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
            Log.e("BottomSheetDebug", "onStateChanged");
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.e("BottomSheetDebug", "onSlide : " + slideOffset);
        }
    };

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int SPEECH_REQUEST_CODE = 0;

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
    Uri.Builder URL = null;/*APIUrls.buildPopularMoviesURL()
            .appendQueryParameter(APIUrls.API_SORT_PARAM, SORT_PREFERENCE);*/
    MovieDisplayAdapter mMovieListDisplayAdapter;
    ArrayList<MovieDataInstance> searchResultMovieList = new ArrayList<>();

    AsyncTask loadFreshDataTask = null, loadMoreDataTask = null;
    FrameLayout mPosterGridFragmentFrameLayout;
    EditText mSearchEditText;

    MovieDataDBHelper mMovieDataDBHelper;
    SQLiteDatabase db;

    CardView mSpeechCard;

    public BottomSheetSearchReveal() {
        // Required empty public constructor
    }


    public static BottomSheetSearchReveal newInstance(String param1, String param2) {
        BottomSheetSearchReveal fragment = new BottomSheetSearchReveal();
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

        mMovieDataDBHelper = new MovieDataDBHelper(getContext());
        db = mMovieDataDBHelper.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_search_reveal, container, false);
        Toolbar mToolBar = (Toolbar) view.findViewById(R.id.searchRevealToolBar);
        AppCompatActivity mActivity = ((AppCompatActivity) getActivity());
        if (getActivity() != null) {
            mActivity.setSupportActionBar(mToolBar);
            if (mActivity.getSupportActionBar() != null)
                mActivity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
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
            Log.e("Saved Instance", "Restoring Instances");

        } else {
            /*if the activity is starting for the first
            time and no saved instances are present
            then load data fom the server*/
                /*Instantiate View Elements*/
            initializeViewElements(view);
            Log.e("Saved Instance", "Loading Data Freshly");
            downloadFreshData();
        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            deleteSearchData();
            performSearch(spokenText);
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void downloadFreshData() {
        if (URL != null) {
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
                    Log.e("Parsed", "ParseDone");
                    Log.e("MovieTypes", "ParseDone");
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
        }
        initializeRecyclerView();
    }

    private void restoreSavedInstances(Bundle savedInstanceState) {
        isStateRestored = true;
        mCurrentCompletelyVisibleItemPosition = savedInstanceState.getIntArray(SCROLL_POSITION_VALUE_KEY);
        mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCEL_KEY);
        initializeRecyclerView();
    }

    private void initializeViewElements(View view) {
        Log.e("OrientationDebug", "OrientationMightHaveChanged");
        mPosterGridFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.moviePosterFragmentFrameLayout);
        mMovieDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.movieDisplayRecyclerView);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.movieSwipeToRefreshLayout);
        /*Should not get Activated on normal swipe down at the beginning of the RecyclerView
        * ThereforeDisable SwipeToRefreshLayout And enable it when required*/
        mSwipeToRefreshLayout.setEnabled(false);
        mSwipeToRefreshLayout.setRefreshing(false);
        mPosterGridFragmentCoOrLayout = (CoordinatorLayout) view.findViewById(R.id.posterGridCoordinatorLayout);
        mSearchEditText = (EditText) view.findViewById(R.id.searchEditText);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d("SearchTEst", v.getText().toString());
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSpeechCard = (CardView) view.findViewById(R.id.speechCard);
        mSpeechCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });
    }

    void performSearch(String searchQuery) {
        deleteSearchData();
        URL = APIUrls.buildSearchURL(searchQuery);
        downloadFreshData();
    }

    void deleteSearchData() {
        if (db.isOpen()) {
            db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA, MovieContract.MovieData.COLUMN_MOVIE_TYPE + " = ?"
                    , new String[]{String.valueOf(MovieContract.MovieData.SEARCHED_MOVIE_TYPE)});
        } else {
            db = mMovieDataDBHelper.getWritableDatabase();
            db.delete(MovieContract.MovieData.TABLE_MOVIE_DATA, MovieContract.MovieData.COLUMN_MOVIE_TYPE + " = ?"
                    , new String[]{String.valueOf(MovieContract.MovieData.SEARCHED_MOVIE_TYPE)});
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
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

        /*mMovieDisplayRecyclerView.setOnScrollChangeListener(new EndlessRecyclerViewScrollListener(
                mStaggeredGridLayoutManager, this, mMovieDisplayRecyclerView
        ));*/
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
        if (SPAN_COUNT != 0)
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
                        BottomSheetSearchReveal.this.mMovieList.add(instance);
                    }

                    mSwipeToRefreshLayout.setRefreshing(false);
                    mSwipeToRefreshLayout.setEnabled(false);
                    EndlessRecyclerViewScrollListener.setLoadingToFalse();
                    getLoaderManager().destroyLoader(LOADER_ID);
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
                            , new String[]{String.valueOf(MovieContract.MovieData.SEARCHED_MOVIE_TYPE)}
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
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        mCurrentCompletelyVisibleItemPosition = new int[SPAN_COUNT];
        mCurrentCompletelyVisibleItemPosition = mStaggeredGridLayoutManager
                .findFirstCompletelyVisibleItemPositions(
                        mCurrentCompletelyVisibleItemPosition
                );
    }

    @Override
    public void onSearchResultAcquired(MovieDataInstance instance) {

    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        Log.e("BottomSheetDebug", "setUpDialog");

        View contentView = View.inflate(getActivity(), R.layout.fragment_search_reveal, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params
                = (CoordinatorLayout.LayoutParams)
                ((View) contentView.getParent()).getLayoutParams();

        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            Log.e("BottomSheetDebug", "settingCallBack");
            ((BottomSheetBehavior) behavior)
                    .setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.search_reveal_menu, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));

            final SearchHelper searchHelper = new SearchHelper(getActivity());

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.e("SearchDebug", "SearchQuery Submitted");
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.e("SearchDebug", "SearchQuery Changed");
                    return false;
                }
            });
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {

                    return false;
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        deleteSearchData();
        db.close();
    }
}
