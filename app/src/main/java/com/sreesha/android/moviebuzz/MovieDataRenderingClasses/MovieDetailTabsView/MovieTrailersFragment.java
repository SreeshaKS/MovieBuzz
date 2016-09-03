package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sreesha.android.moviebuzz.DataHandlerClasses.CursorRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.TrailersRecyclerViewCursorAdapter;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
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

public class MovieTrailersFragment extends Fragment implements MovieTabsDetailFragment.MovieTrailersFragmentInterface {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    CardView mInternetConnectionErrorMessageCard;
    CardView mDataRefreshCard;

    private final static String MOVIE_PARCELABLE_SAVED_KEY = "movieParcelableDetailViewFragmentKey";
    private boolean mIsStateRestored = false;

    RecyclerView mTrailersRecyclerView;
    private final int TRAILERS_LOADER_ID = 12760;
    TrailersRecyclerViewCursorAdapter mTrailersRecyclerViewAdapter
            = new TrailersRecyclerViewCursorAdapter(getActivity(), null);
    MovieDataInstance mMovieData;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    public MovieTrailersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieTrailersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieTrailersFragment newInstance(String param1, String param2) {
        MovieTrailersFragment fragment = new MovieTrailersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (savedInstanceState != null) {
            mMovieData = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
    }

    public void setMovieData(MovieDataInstance mMovieData) {
        this.mMovieData = mMovieData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);
        initializeViewElements(view);
        setUpAndInitializeRecyclerView(mTrailersRecyclerView
                , new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
                , mTrailersRecyclerViewAdapter);
        return view;
    }


    private void initializeViewElements(View view) {
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.trailersRecyclerView);
        mInternetConnectionErrorMessageCard = (CardView) view.findViewById(R.id.errorMessageDisplayCard);
        mDataRefreshCard = (CardView) view.findViewById(R.id.refreshCard);
        mInternetConnectionErrorMessageCard.setVisibility(View.INVISIBLE);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshLayout);
        mSwipeToRefreshLayout.setRefreshing(false);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMovieTrailersReviews();
            }
        });
        mDataRefreshCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (
                        (MovieTabsDetailFragment)
                                (getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                ).OnUIReadyToBeUpdated(UIUpdatableInterface.TRAILERS_FRAGMENT);
            }
        });
    }

    private void setUpAndInitializeRecyclerView(
            RecyclerView recyclerView
            , LinearLayoutManager recyclerViewLayoutManager
            , CursorRecyclerViewAdapter cursorRecyclerViewAdapter) {
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(cursorRecyclerViewAdapter);
        if (mIsStateRestored) {
            getLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, mLoaderCallBacks);
        } else {
            ((MovieTabsDetailFragment)
                    (getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
            ).OnUIReadyToBeUpdated(UIUpdatableInterface.TRAILERS_FRAGMENT);
        }
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
    public void OnMovieDataChanged(MovieDataInstance mMovieData) {
        try {
            if (mTrailersRecyclerViewAdapter != null && mMovieData != null) {
                notifyUIOfConnectionStatus(true);
                this.mMovieData = mMovieData;
                getLoaderManager().destroyLoader(TRAILERS_LOADER_ID);
                getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, mLoaderCallBacks);
            } else if (mMovieData == null) {
                notifyUIOfConnectionStatus(false);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void fetchMovieTrailersReviews() {
        if (null != mMovieData) {
            new DownloadData(new AsyncResult() {
                @Override
                public void onResultJSON(JSONObject object) throws JSONException {

                }

                @Override
                public void onResultString(String stringObject) {
                    if (getActivity() != null) {
                        if (MoviePosterGridActivity.isInTwoPaneMode()) {
                            ((MoviePosterGridActivity) getActivity())
                                    .showNetworkConnectivityDialogue("Please Connect to a working Network");
                        } else {

                        }
                    }
                }

                @Override
                public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {

                }

                @Override
                public void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList) {
                    if (getActivity() != null) {
                        if (trailerList.size() != 0) {
                            getLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, mLoaderCallBacks);
                            mSwipeToRefreshLayout.setRefreshing(false);
                        } else if (getActivity() != null && trailerList.size() == 0) {
                            Toast.makeText(getActivity(), "NO Trailers Found", Toast.LENGTH_SHORT).show();
                            mSwipeToRefreshLayout.setRefreshing(false);
                        }
                    }
                }

                @Override
                public void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList) {
                }
            }
                    , getActivity()
            ).execute(
                    null
                    , APIUrls.buildMovieTrailerURL(mMovieData.getMovieID() + "").toString()
            );
        }
    }

    public void notifyUIOfConnectionStatus(boolean isInternetAvailable) {
        if (mInternetConnectionErrorMessageCard!=null &&mTrailersRecyclerView!=null) {
            if (isInternetAvailable) {
                mInternetConnectionErrorMessageCard.setVisibility(View.INVISIBLE);
                mTrailersRecyclerView.setVisibility(View.VISIBLE);
                mTrailersRecyclerView.setEnabled(true);
            } else {
                mInternetConnectionErrorMessageCard.setVisibility(View.VISIBLE);
                mTrailersRecyclerView.setVisibility(View.INVISIBLE);
                mTrailersRecyclerView.setEnabled(false);
            }
        }
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> mLoaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mMovieData != null) {
                switch (id) {
                    case TRAILERS_LOADER_ID:
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.MovieTrailers
                                .buildMovieTrailerUriWithMovieId(String.valueOf(mMovieData.getMovieID()))
                                , null
                                , null
                                , null
                                , null
                        );
                    default:
                        return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case TRAILERS_LOADER_ID:
                    if (data.getCount() == 0) {
                        fetchMovieTrailersReviews();
                    } else {
                        mTrailersRecyclerViewAdapter.swapCursor(data);
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

        }
    };
}
