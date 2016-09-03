package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

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
import android.widget.FrameLayout;

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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PopularPeopleFragment extends Fragment {
    private final static String POPULAR_PEOPLE_DATA_PARCELABLE_SAVED_KEY
            = "popularPeopleDataParcelableDetailViewFragmentKey";

    RecyclerView mPopularPeopleRecyclerView;
    PopularPeopleRecyclerAdapter mPopPeopleRecyclerAdapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    ArrayList<PopularPeopleInstance> mPopularPeopleArrayList;
    PopularPeopleInstance mPopularPeopleData = null;

    FrameLayout mCastFragmentFrameLayout;
    SwipeRefreshLayout mSwipeToRefreshLayout;

    int SPAN_COUNT = 2;
    int POSTER_WIDTH = 200;

    private boolean mIsStateRestored = false;

    DownloadMovieSpecifics mMovieSpecificsDownloadTask;

    public PopularPeopleFragment() {

    }

    public static PopularPeopleFragment newInstance(String param1, String param2) {
        return new PopularPeopleFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mPopularPeopleArrayList = new ArrayList<PopularPeopleInstance>();
        } else {
            mIsStateRestored = true;
            mPopularPeopleArrayList = savedInstanceState.getParcelableArrayList(POPULAR_PEOPLE_DATA_PARCELABLE_SAVED_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(POPULAR_PEOPLE_DATA_PARCELABLE_SAVED_KEY, mPopularPeopleArrayList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater
            , @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_people, container, false);
        initializeViewElements(view);
        return view;
    }

    public void initializeViewElements(View view) {
        mPopularPeopleRecyclerView = (RecyclerView) view.findViewById(R.id.popularPeopleDisplayRecyclerView);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefreshLayout);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mSwipeToRefreshLayout.isRefreshing()) {
                    mSwipeToRefreshLayout.setRefreshing(false);
                    downloadData();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsStateRestored) {
            loadRecyclerViewWithData();
        } else {
            downloadData();
        }
    }

    public void loadRecyclerViewWithData() {
        mPopPeopleRecyclerAdapter = new PopularPeopleRecyclerAdapter(mPopularPeopleArrayList, getActivity());
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        mPopularPeopleRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mPopularPeopleRecyclerView.setAdapter(mPopPeopleRecyclerAdapter);
    }

    public void downloadData() {
        if (!mSwipeToRefreshLayout.isRefreshing()) {
            mSwipeToRefreshLayout.setRefreshing(true);
            mMovieSpecificsDownloadTask = new DownloadMovieSpecifics(
                    getActivity()
                    , new AsyncMovieSpecificsResults() {
                @Override
                protected void onResultJSON(JSONObject object) throws JSONException {
                }

                @Override
                protected void onResultString(String stringObject, String errorString, String parseStatus) {

                }

                @Override
                protected void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList) {

                }

                @Override
                protected void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList) {

                }

                @Override
                protected void onResultParsedIntoPopularPersonInfo(
                        ArrayList<PopularPeopleInstance> popularPeopleInstanceArrayList
                ) {
                    if (getActivity() != null) {
                        mSwipeToRefreshLayout.setRefreshing(false);
                        mPopularPeopleArrayList = popularPeopleInstanceArrayList;
                        loadRecyclerViewWithData();
                    }
                }

                @Override
                protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList, ArrayList<MovieImage> posterList) {

                }

                @Override
                protected void onResultParsedIntoPersonData(PersonInstance instance) {

                }
            }
            );
            mMovieSpecificsDownloadTask
                    .execute(APIUrls.buildPopularCastCrewURL().build().toString());
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
