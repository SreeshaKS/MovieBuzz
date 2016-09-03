package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.OnMoreReviewDataRequestedListener;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.ReviewRecyclerViewCursorAdapter;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncResult;
import com.sreesha.android.moviebuzz.Networking.DownloadData;
import com.sreesha.android.moviebuzz.Networking.FBC;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.Networking.User;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment implements MovieTabsDetailFragment.MovieReviewsFragmentInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    MovieDataInstance mMovieData;
    RecyclerView mReviewsRecyclerView;
    ReviewRecyclerViewCursorAdapter reviewsRecyclerViewAdapter;
    String mCurrentMovieName = "defaultMovieName";
    CardView internetConnectionErrorMessageCard;
    CardView dataRefreshCard;
    private final int REVIEWS_LOADER_ID = 12761;
    OnMoreReviewDataRequestedListener reviewDataRequestedListener;
    private final static String MOVIE_PARCELABLE_SAVED_KEY = "movieParcelableDetailViewFragmentKey";
    private boolean mIsStateRestored = false;
    FrameLayout mReviewsFrameLayout;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    TextView mNetworkErrorMessageTextView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public ReviewsFragment() {
        // Required empty public constructor
    }

    private DatabaseReference mDatabase;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
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
            mMovieData = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("FireBaseAuth", "User Logged In : "
                    + user.getDisplayName() + "\n" + user.getUid());
            final String userId = user.getUid();
            mDatabase.child(FBC.USER).child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);
                            Log.d("FBCDebug", dataSnapshot.toString());
                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        } else {
            Log.d("FireBaseAuth", "User Not Logged In");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        initializeViewElements(view);
        setUpAndInitializeRecyclerView(mReviewsRecyclerView
                , new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    private void initializeLoaders() {
        getLoaderManager().initLoader(REVIEWS_LOADER_ID
                , null, loaderCallBacks);
    }

    private void setUpAndInitializeRecyclerView(
            RecyclerView recyclerView
            , LinearLayoutManager recyclerViewLayoutManager) {
        reviewsRecyclerViewAdapter = new ReviewRecyclerViewCursorAdapter(getActivity(), null, mMovieData);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(reviewsRecyclerViewAdapter);
        reviewsRecyclerViewAdapter.setMovieDataInstance(mMovieData);
        if (mIsStateRestored) {
            try {
                getLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, loaderCallBacks);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            (
                    (MovieTabsDetailFragment)
                            (getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
            ).OnUIReadyToBeUpdated(UIUpdatableInterface.REVIEWS_FRAGMENT);
        }
    }

    private void initializeViewElements(View view) {
        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.reviewRecyclerView);
        internetConnectionErrorMessageCard = (CardView) view.findViewById(R.id.errorMessageDisplayCard);
        dataRefreshCard = (CardView) view.findViewById(R.id.refreshCard);
        internetConnectionErrorMessageCard.setVisibility(View.INVISIBLE);
        mReviewsFrameLayout = (FrameLayout) view.findViewById(R.id.reviewsFrameLayout);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshLayout);
        mSwipeToRefreshLayout.setRefreshing(false);
        mNetworkErrorMessageTextView = (TextView) view.findViewById(R.id.networkErrorMessageTextView);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMovieTrailersReviews();
            }
        });
        dataRefreshCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (
                        (MovieTabsDetailFragment)
                                (getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                ).OnUIReadyToBeUpdated(UIUpdatableInterface.REVIEWS_FRAGMENT);
            }
        });
    }

    public void notifyUIOfConnectionStatus(boolean isInternetAvailable) {
        if (isInternetAvailable) {
            internetConnectionErrorMessageCard.setVisibility(View.INVISIBLE);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setEnabled(true);
        } else {
            mNetworkErrorMessageTextView.setText(
                    getResources().getString(R.string.please_connect_to_a_working_internet_connection_string));
            internetConnectionErrorMessageCard.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.INVISIBLE);
            mReviewsRecyclerView.setEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setMovieData(MovieDataInstance mMovieData) {
        this.mMovieData = mMovieData;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void OnMovieDataChanged(MovieDataInstance movieData) {
        if (reviewsRecyclerViewAdapter != null && mReviewsRecyclerView != null && movieData != null) {
            mMovieData = movieData;
            getLoaderManager().destroyLoader(REVIEWS_LOADER_ID);
            getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, loaderCallBacks);
            reviewsRecyclerViewAdapter.setMovieDataInstance(mMovieData);
            notifyUIOfConnectionStatus(true);
        } else if (movieData == null) {
            notifyUIOfConnectionStatus(false);
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
                        if (!stringObject.equals(DownloadData.RESULT_EMPTY_DATA_SET)) {

                            if (MoviePosterGridActivity.isInTwoPaneMode()) {
                                ((MoviePosterGridActivity) getActivity())
                                        .showNetworkConnectivityDialogue("Please Connect to a working Network");
                            } else {
                                if (internetConnectionErrorMessageCard != null) {
                                    internetConnectionErrorMessageCard.setVisibility(View.VISIBLE);
                                    mReviewsRecyclerView.setVisibility(View.GONE);
                                    mReviewsRecyclerView.setEnabled(false);
                                }
                            }
                        } else {
                            Snackbar.make(mReviewsRecyclerView, "No Reviews Found", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {

                }

                @Override
                public void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList) {
                }

                @Override
                public void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList) {
                    if (getActivity() != null && reviewList.size() != 0) {
                        if (internetConnectionErrorMessageCard.getVisibility() == View.VISIBLE) {
                            internetConnectionErrorMessageCard.setVisibility(View.GONE);
                        }
                        getLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, loaderCallBacks);
                    } else if (getActivity() != null && reviewList.size() == 0) {
                        internetConnectionErrorMessageCard.setVisibility(View.VISIBLE);
                        mNetworkErrorMessageTextView.setText(R.string.no_reviews_found_string);
                    }
                    mSwipeToRefreshLayout.setRefreshing(false);
                }
            }
                    , getActivity()
            ).execute(
                    APIUrls.buildMovieReviewsURL(mMovieData.getMovieID() + "").toString()
                    , null
            );
        }
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mMovieData != null) {
                switch (id) {
                    case REVIEWS_LOADER_ID:
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.MovieReviews.buildMovieReviewUriWithMovieId(String.valueOf(mMovieData.getMovieID()))
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
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case REVIEWS_LOADER_ID:
                    if (data.getCount() == 0) {
                        fetchMovieTrailersReviews();
                    } else {
                        if (internetConnectionErrorMessageCard.getVisibility() == View.VISIBLE) {
                            internetConnectionErrorMessageCard.setVisibility(View.GONE);
                        }
                        if (reviewsRecyclerViewAdapter != null) {
                            reviewsRecyclerViewAdapter.swapCursor(data);
                        }
                    }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    public void setCurrentMovieName(String currentMovieName) {
        mCurrentMovieName = currentMovieName;
    }
}
