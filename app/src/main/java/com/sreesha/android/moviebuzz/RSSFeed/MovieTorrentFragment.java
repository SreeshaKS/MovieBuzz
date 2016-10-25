package com.sreesha.android.moviebuzz.RSSFeed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

public class MovieTorrentFragment extends Fragment {
    public static final String MOVIE_TORRENT_FRAGMENT_TAG = "MovieTorrentFragment";
    private static final String ARG_PARAM1 = "param1";

    ArrayList<YTSMovie> mYTSMovieArrayList;
    YTSMovieAdapter mMovieAdapter;
    LinearLayoutManager mLinearLayoutManager;

    public MovieTorrentFragment() {
        // Required empty public constructor
    }

    public static MovieTorrentFragment newInstance(ArrayList<YTSMovie> mYTSMovieArrayList) {
        MovieTorrentFragment fragment = new MovieTorrentFragment();
        Bundle args = new Bundle();
        Log.d("YTSArrayList", "Setting Arguments\t" + mYTSMovieArrayList.size());

        args.putParcelableArrayList(ARG_PARAM1, mYTSMovieArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    boolean isStateRestored = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isStateRestored = true;
            mYTSMovieArrayList = savedInstanceState.getParcelableArrayList(ARG_PARAM1);
        } else if (getArguments() != null) {
            mYTSMovieArrayList = getArguments().getParcelableArrayList(ARG_PARAM1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_PARAM1, mYTSMovieArrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_torrent, container, false);
        initializeViewElements(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isStateRestored) {
            initRecyclerView();
        } else {
            initRecyclerView();
        }
    }

    RecyclerView mYTSMovieRecyclerView;

    private void initializeViewElements(View view) {
        mYTSMovieRecyclerView = (RecyclerView) view.findViewById(R.id.movieTorrentRecyclerView);
    }

    void initRecyclerView() {
        mMovieAdapter = new YTSMovieAdapter(mYTSMovieArrayList);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mYTSMovieRecyclerView.setLayoutManager(mLinearLayoutManager);
        mYTSMovieRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
