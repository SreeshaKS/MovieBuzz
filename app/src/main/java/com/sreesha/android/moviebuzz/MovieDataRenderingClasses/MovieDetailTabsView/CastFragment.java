package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncMovieSpecificsResults;
import com.sreesha.android.moviebuzz.Networking.CastDataInstance;
import com.sreesha.android.moviebuzz.Networking.CrewDataInstance;
import com.sreesha.android.moviebuzz.Networking.DownloadMovieSpecifics;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.Networking.PersonInstance;
import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CastFragment extends Fragment implements MovieTabsDetailFragment.MovieDetailedDataFragmentInterface {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView mCastRecyclerView;
    CastDataCursorAdapter mCastRecyclerViewCursorAdapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private static final int CAST_LOADER_ID = 0;

    MovieDataInstance mMovieData = null;
    CastDataInstance mCastData = null;
    private boolean mIsStateRestored = false;
    private final static String MOVIE_DATA_PARCELABLE_SAVED_KEY = "movieDataParcelableDetailViewFragmentKey";
    private final static String MOVIE_CAST_PARCELABLE_SAVED_KEY = "movieCastParcelableDetailViewFragmentKey";
    DownloadMovieSpecifics mMovieSpecificsDownloadTask;
    FrameLayout mCastFragmentFrameLayout;
    int SPAN_COUNT = 2;
    int POSTER_WIDTH = 200;
    SwipeRefreshLayout mSwipeToRefreshLayout;

    public CastFragment() {
        // Required empty public constructor
    }

    public static CastFragment newInstance(String param1, String param2) {
        CastFragment fragment = new CastFragment();
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
        if (savedInstanceState != null) {
            mMovieData = savedInstanceState.getParcelable(MOVIE_DATA_PARCELABLE_SAVED_KEY);
            //mCastData = savedInstanceState.getParcelable(MOVIE_CAST_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DATA_PARCELABLE_SAVED_KEY, mMovieData);
        //outState.putParcelable(MOVIE_DATA_PARCELABLE_SAVED_KEY,mMovieData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cast, container, false);
        initializeViewElements(view);
        if (mMovieData != null) {

            if (mIsStateRestored) {

            } else {
                initializeRecyclerView();
                (
                        (MovieTabsDetailFragment)
                                (getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                ).OnUIReadyToBeUpdated(UIUpdatableInterface.MOVIE_CAST_TABS_FRAGMENT);
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CAST_LOADER_ID, null, mLoaderCallBacks);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsStateRestored) {
            updateUIWithMovieData();
            getLoaderManager().destroyLoader(CAST_LOADER_ID);
            getLoaderManager().initLoader(CAST_LOADER_ID, null, mLoaderCallBacks);
        } else {
            ((MovieTabsDetailFragment)
                    (getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
            ).OnUIReadyToBeUpdated(UIUpdatableInterface.MOVIE_CAST_TABS_FRAGMENT);
        }
        if (mCastRecyclerView != null) {
            mCastRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (getActivity() != null) {
                                try {
                                    computeAndRegisterSpanCount();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    private void initializeViewElements(View view) {
        mCastRecyclerView = (RecyclerView) view.findViewById(R.id.castRecyclerView);
        mCastFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.castFragmentFrameLayout);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshLayout);
        mSwipeToRefreshLayout.setRefreshing(false);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadMovieSpecificData();
            }
        });
    }

    private void initializeRecyclerView() {
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mCastRecyclerViewCursorAdapter = new CastDataCursorAdapter(getActivity(), null);
        mCastRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mCastRecyclerView.setAdapter(mCastRecyclerViewCursorAdapter);
    }

    private void computeAndRegisterSpanCount() {
        try {
            if (MoviePosterGridActivity.isInTwoPaneMode()) {
                SPAN_COUNT = (int)Math.ceil((convertPixelsToDP(
                        mCastFragmentFrameLayout.getWidth())
                        + convertPixelsToDP(
                        (int) getResources()
                                .getDimension(R.dimen.movie_poster_margin)
                )
                ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
            } else {
                SPAN_COUNT = (int) Math.ceil((convertPixelsToDP(
                        mCastFragmentFrameLayout.getWidth())
                        + convertPixelsToDP(
                        (int) getResources()
                                .getDimension(R.dimen.movie_poster_margin)
                )
                ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
            }
            if (SPAN_COUNT > 0 && mStaggeredGridLayoutManager != null)
                mStaggeredGridLayoutManager.setSpanCount(SPAN_COUNT);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
    }

    /*Density-independent pixels is equal to one physical pixel on a 160dpi screen->Considered As the Baseline
                    Therefore for a screen with 'x' dpi and 'px' pixels(say for width)
                    :::
                    "(x*px/160)" is the number in device independent pixels(i.e width in device independent pixels)*/
    private float convertPixelsToDP(int width) {
        return (width / /*screen Width in Device Independent pixels*/ (getResources().getDisplayMetrics().densityDpi / 160f));
    }

    private void updateUIWithMovieData() {
        initializeRecyclerView();
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> mLoaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case CAST_LOADER_ID:
                    if (mMovieData != null) {
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.CastData.MOVIE_CAST_CONTENT_URI
                                , null
                                , MovieContract.CastData.COLUMN_MOVIE_ID + " =?"
                                , new String[]{String.valueOf(mMovieData.getMovieID())}
                                , null
                        );
                    } else {
                        Log.e("CastCrewDebug", "Movie Data NULL");
                    }
                    break;
               /* case CREW_LOADER_ID:
                    new CursorLoader(getActivity()
                            , MovieContract.CrewData.MOVIE_CREW_CONTENT_URI
                            , null
                            , MovieContract.CastData.COLUMN_MOVIE_ID + " =?"
                            , new String[]{String.valueOf(mMovieData.getMovieID())}
                            , null
                    );
                    break;*/
                default:
                    return null;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case CAST_LOADER_ID:
                    if (!(data.getCount() == 0)) {
                        if (mCastRecyclerViewCursorAdapter != null) {
                            mCastRecyclerViewCursorAdapter.swapCursor(data);
                        } else {
                            initializeRecyclerView();
                            mCastRecyclerViewCursorAdapter.swapCursor(data);
                        }
                    } else {
                        downloadMovieSpecificData();
                    }
                    break;
                default:
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void downloadMovieSpecificData() {
        mMovieSpecificsDownloadTask =
                new DownloadMovieSpecifics(
                        getActivity(),
                        new AsyncMovieSpecificsResults() {
                            @Override
                            protected void onResultJSON(JSONObject object) throws JSONException {
                            }

                            @Override
                            protected void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList) {
                                if (getActivity() != null) {
                                    if (castList.size() != 0) {
                                        getLoaderManager().restartLoader(CAST_LOADER_ID, null, mLoaderCallBacks);
                                    } else if (castList.size() == 0) {
                                        Toast.makeText(getActivity(), "NO Cast Found", Toast.LENGTH_SHORT).show();
                                    }
                                    mSwipeToRefreshLayout.setRefreshing(false);
                                }
                            }

                            @Override
                            protected void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList) {
                                if (getActivity() != null) {
                                    if (crewList.size() != 0) {
                                    } else if (crewList.size() == 0) {
                                        Toast.makeText(getActivity(), "No Crew Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            protected void onResultString(String stringObject, String errorString, String parseStatus) {
                                if (getActivity() != null) {

                                }
                            }

                            @Override
                            protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList, ArrayList<MovieImage> posterList) {

                            }

                            @Override
                            protected void onResultParsedIntoPopularPersonInfo(ArrayList<PopularPeopleInstance> popularPeopleInstanceArrayList) {

                            }

                            @Override
                            protected void onResultParsedIntoPersonData(PersonInstance instance) {

                            }
                        }
                );
        mMovieSpecificsDownloadTask
                .execute(
                        APIUrls
                                .buildCastCrewCreditsURL(String.valueOf(mMovieData.getMovieID())
                                )
                                .build()
                                .toString());
    }

    public void setMovieData(MovieDataInstance mMovieData) {
        this.mMovieData = mMovieData;
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
    public void OnMovieDataChanged(MovieDataInstance movieInstance) {
        if (mCastRecyclerViewCursorAdapter != null && mMovieData != null) {
            try {
                mMovieData = movieInstance;

                //getLoaderManager().destroyLoader(CAST_LOADER_ID);
                //getLoaderManager().initLoader(CAST_LOADER_ID, null, mLoaderCallBacks);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mMovieSpecificsDownloadTask != null) {
            if (!mMovieSpecificsDownloadTask.isCancelled()) {
                mMovieSpecificsDownloadTask.cancel(true);
                mMovieSpecificsDownloadTask = null;
            }
        }
    }
}
