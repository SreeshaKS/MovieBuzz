package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;


import android.os.Bundle;
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
import android.widget.TextView;

import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MoviePhotosFragment;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailFragment;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.PhotosRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.UIUpdatableInterface;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncMovieSpecificsResults;
import com.sreesha.android.moviebuzz.Networking.CastDataInstance;
import com.sreesha.android.moviebuzz.Networking.CrewDataInstance;
import com.sreesha.android.moviebuzz.Networking.DownloadMovieSpecifics;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.Networking.PersonInstance;
import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersonImagesFragment extends Fragment {
    public static final String PERSON_IMAGES_LIST_KEY = "personImageListKey";
    boolean mIsRestored = false;
    PersonInstance mPersonDataInstance;

    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    PersonPhotosRVAdapter mPhotosRecyclerViewAdapter;
    RecyclerView mPhotosRecyclerView;
    FrameLayout mPhotosFragmentFrameLayout;
    SwipeRefreshLayout mSwipeToRefreshLayout;

    DownloadMovieSpecifics mMovieSpecificsDownloadTask;

    ArrayList<PersonImage> mPersonImageList;

    int SPAN_COUNT = 2;
    int POSTER_WIDTH = 200;

    public PersonImagesFragment() {
        // Required empty public constructor
    }

    public static PersonImagesFragment newInstance(PersonInstance mPersonDataInstance) {
        PersonImagesFragment fragment = new PersonImagesFragment();
        Bundle args = new Bundle();
        //TODO:Add Arguments to Fragment
        args.putParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY, mPersonDataInstance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = new Bundle();
        if (savedInstanceState != null) {
            mBundle = savedInstanceState;
            mPersonImageList = savedInstanceState.getParcelableArrayList(PERSON_IMAGES_LIST_KEY);
            mIsRestored = true;
        }
        if (getArguments() != null) {
            mBundle = getArguments();
        }

        mPersonDataInstance
                = mBundle.getParcelable(
                PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsRestored) {
            initializeRecyclerView();
        } else {
            downloadMovieSpecificData();
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

    private void computerAndRegisterSpanCount() {
        try {
            if (MoviePosterGridActivity.isInTwoPaneMode()) {
                SPAN_COUNT = Math.round((convertPixelsToDP(
                        mPhotosFragmentFrameLayout.getWidth())
                        + convertPixelsToDP(
                        (int) getResources()
                                .getDimension(R.dimen.movie_poster_margin)
                )
                ) / POSTER_WIDTH);/*width of each image poster image for a phone*/
            } else {
                SPAN_COUNT = (int) Math.round((convertPixelsToDP(
                        mPhotosFragmentFrameLayout.getWidth())
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(
                PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY
                , mPersonDataInstance
        );
        outState.putParcelableArrayList(PERSON_IMAGES_LIST_KEY, mPersonImageList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_images, container, false);
        initializeViewElements(view);
        return view;
    }

    private void initializeViewElements(View view) {
        mPhotosRecyclerView = (RecyclerView) view.findViewById(R.id.photosRecyclerView);
        mPhotosFragmentFrameLayout = (FrameLayout) view.findViewById(R.id.personPhotosFrameLayout);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.photosSwipeRefreshLayout);
        mSwipeToRefreshLayout.setRefreshing(false);
        initializeRecyclerView();
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadMovieSpecificData();
            }
        });
    }

    private void initializeRecyclerView() {
        Log.d("RVDebug", "RecyclerView Initilizing");
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        if (mPersonImageList == null || mPersonImageList.isEmpty())
            mPhotosRecyclerViewAdapter = new PersonPhotosRVAdapter(null);
        else {
            mPhotosRecyclerViewAdapter = new PersonPhotosRVAdapter(mPersonImageList);
        }

        mPhotosRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mPhotosRecyclerView.setAdapter(mPhotosRecyclerViewAdapter);
    }

    private void downloadMovieSpecificData() {
        if (mPersonDataInstance != null) {
            mMovieSpecificsDownloadTask =
                    new DownloadMovieSpecifics(
                            getActivity(),
                            new AsyncMovieSpecificsResults() {
                                @Override
                                protected void onResultJSON(JSONObject object) throws JSONException {
                                }

                                @Override
                                protected void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList) {
                                }

                                @Override
                                protected void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList) {
                                }

                                @Override
                                protected void onResultString(String stringObject, String errorString, String parseStatus) {

                                }

                                @Override
                                protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList
                                        , ArrayList<MovieImage> posterList, ArrayList<PersonImage> personImageList) {
                                    Log.d("RVDebug", "Data Acquired \n Count : " + personImageList
                                            .size());
                                    if (getActivity() != null) {
                                        PersonImagesFragment.this.mPersonImageList = personImageList;
                                        initializeRecyclerView();
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
            Log.d("RVDebug", "URL : " + APIUrls
                    .buildPersonPhotosURL(
                            String.valueOf(mPersonDataInstance.getID()))
                    .build().toString());
            mMovieSpecificsDownloadTask
                    .execute(
                            APIUrls
                                    .buildPersonPhotosURL(
                                            String.valueOf(mPersonDataInstance.getID()))
                                    .build().toString()
                    );
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMovieSpecificsDownloadTask != null
                && mMovieSpecificsDownloadTask.isCancelled()) {
            mMovieSpecificsDownloadTask.cancel(true);
            mMovieSpecificsDownloadTask = null;
        } else {
            mMovieSpecificsDownloadTask = null;
        }
    }
}
