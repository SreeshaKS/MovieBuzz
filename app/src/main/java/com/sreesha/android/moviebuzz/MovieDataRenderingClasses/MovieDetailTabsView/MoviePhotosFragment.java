package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

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

public class MoviePhotosFragment extends Fragment implements MovieTabsDetailFragment.MovieDetailedDataFragmentInterface {


    public static final String MOVIE_PARCELABLE_KEY = "similarMoviesParcelableKey";
    MovieDataInstance mMovieDataInstance;

    public static final String MOVIE_BACKDROP_PARCELABLE_KEY = "movieBackDropParcelableKey";
    public static final String MOVIE_POSTER_PARCELABLE_KEY = "moviePosterParcelableKey";

    ArrayList<MovieImage> backDropsList;
    ArrayList<MovieImage> posterList;
    private boolean mIsStateRestored = false;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    PhotosRecyclerViewAdapter mPhotosRecyclerViewAdapter;
    RecyclerView mPhotosRecyclerView;
    FrameLayout mPhotosFragmentFrameLayout;
    SwipeRefreshLayout mSwipeToRefreshLayout;
    DownloadMovieSpecifics mMovieSpecificsDownloadTask;

    int SPAN_COUNT = 2;
    int POSTER_WIDTH = 200;

    public MoviePhotosFragment() {
        // Required empty public constructor
    }

    public static MoviePhotosFragment newInstance(MovieDataInstance mMovieDataInstance) {
        MoviePhotosFragment fragment = new MoviePhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_PARCELABLE_KEY, mMovieDataInstance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            backDropsList = savedInstanceState.getParcelableArrayList(MOVIE_BACKDROP_PARCELABLE_KEY);
            posterList = savedInstanceState.getParcelableArrayList(MOVIE_POSTER_PARCELABLE_KEY);
            mMovieDataInstance = savedInstanceState.getParcelable(MOVIE_PARCELABLE_KEY);
            mIsStateRestored = true;
        } else {
            if (getArguments() != null) {
                mMovieDataInstance = getArguments().getParcelable(MOVIE_PARCELABLE_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("CastCrewDebug", "onSavednStanceState");
        outState.putParcelableArrayList(MOVIE_BACKDROP_PARCELABLE_KEY, backDropsList);
        outState.putParcelableArrayList(MOVIE_POSTER_PARCELABLE_KEY, posterList);
        outState.putParcelable(MOVIE_PARCELABLE_KEY, mMovieDataInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_photos, container, false);
        initializeViewElements(view);
        if (mMovieDataInstance != null) {

            if (mIsStateRestored) {
                Log.e("CastCrewDebug", "StateRestored");

            } else {

            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        downloadMovieSpecificData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMovieDataInstance != null) {
            Log.e("onCreateView", "UpdatingUI and Fetching Data");
            if (mIsStateRestored) {
                Log.e("MovieDetailDisplayFrag", "Restoring and updating");
            } else {
                Log.e("MovieDetailDisplayFrag", "Calling UpDataUIReady");
                (
                        (MovieTabsDetailFragment)
                                (getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                ).OnUIReadyToBeUpdated(UIUpdatableInterface.MOVIE_PHOTOS_FRAGMENT);
            }
        }
        if (mPhotosRecyclerView != null) {
            mPhotosRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (getActivity() != null) {
                                computerAndRegisterSpanCount();
                            }
                        }
                    });
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

    private void initializeViewElements(View view) {
        mPhotosRecyclerView = (RecyclerView) view.findViewById(R.id.photosRecyclerView);
        mPhotosFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.moviePhotosFrameLayout);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.photosSwipeRefreshLayout);
        mSwipeToRefreshLayout.setRefreshing(false);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadMovieSpecificData();
            }
        });
    }

    private void initializeRecyclerView() {

        Log.e("CastCrewDebug", "initializeRecyclerView()");
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mPhotosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(backDropsList, posterList);

        mPhotosRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mPhotosRecyclerView.setAdapter(mPhotosRecyclerViewAdapter);
    }

    private void computerAndRegisterSpanCount() {
        Log.e("Span Debug", "Screen Width : " + convertPixelsToDP(
                mPhotosFragmentFrameLayout.getWidth()) + "Computer Value" + (convertPixelsToDP(
                mPhotosFragmentFrameLayout.getWidth())
                + convertPixelsToDP(
                (int) getResources()
                        .getDimension(R.dimen.movie_poster_margin)
        )
        ));
        if (MoviePosterGridActivity.isInTwoPaneMode()) {
            SPAN_COUNT = Math.round((convertPixelsToDP(
                    mPhotosFragmentFrameLayout.getWidth())
                    + convertPixelsToDP(
                    (int) getResources()
                            .getDimension(R.dimen.movie_poster_margin)
            )
            ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
        } else {
            SPAN_COUNT = (int) Math.ceil((convertPixelsToDP(
                    mPhotosFragmentFrameLayout.getWidth())
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

    @Override
    public void OnMovieDataChanged(MovieDataInstance movieInstance) {
        if (mPhotosRecyclerViewAdapter != null && mMovieDataInstance != null) {
            try {
                Log.e("CastCrewDebug", "OnMovieDataCHangedCalled");
                mMovieDataInstance = movieInstance;
                downloadMovieSpecificData();
                Log.e("CastCrewDebug", "Calling Loader Manager");
            } catch (IllegalStateException e) {
                Log.e("CastCrewDebug", "Exception Inside OnMovieDataChanged : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void downloadMovieSpecificData() {
        if (mMovieDataInstance != null) {
            mMovieSpecificsDownloadTask =
                    new DownloadMovieSpecifics(
                            getActivity(),
                            new AsyncMovieSpecificsResults() {
                                @Override
                                protected void onResultJSON(JSONObject object) throws JSONException {
                                    Log.e("MoviePhotoDebug", object.toString());
                                }

                                @Override
                                protected void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList) {
                                }

                                @Override
                                protected void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList) {
                                }

                                @Override
                                protected void onResultString(String stringObject, String errorString, String parseStatus) {
                                    Log.e("MoviePhotoDebug", "Error String : " + errorString
                                            + "\nParse Status : " + parseStatus);
                                }

                                @Override
                                protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList, ArrayList<MovieImage> posterList) {
                                    if (getActivity() != null) {
                                        MoviePhotosFragment.this.backDropsList = backDropsList;
                                        MoviePhotosFragment.this.posterList = posterList;
                                        Log.e("MoviePhotoDebug", "");

                                        initializeRecyclerView();
                                        (
                                                (MovieTabsDetailFragment)
                                                        (getActivity()
                                                                .getSupportFragmentManager()
                                                                .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                                        ).OnMovieImageDataLoaded(backDropsList);
                                        mSwipeToRefreshLayout.setRefreshing(false);
                                    }
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
                                    .buildMoviePhotosURL(String.valueOf(mMovieDataInstance.getMovieID()))
                                    .build().toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMovieSpecificsDownloadTask != null) {
            if (!mMovieSpecificsDownloadTask.isCancelled()) {
                mMovieSpecificsDownloadTask.cancel(true);
                mMovieSpecificsDownloadTask = null;
            } else {
                mMovieSpecificsDownloadTask = null;
            }
        }
    }
}
