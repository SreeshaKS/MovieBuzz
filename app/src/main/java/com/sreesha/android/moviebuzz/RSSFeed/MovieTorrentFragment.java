package com.sreesha.android.moviebuzz.RSSFeed;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

        initializeListeners();
    }

    void initializeListeners() {
    }

    void initRecyclerView() {
        mMovieAdapter = new YTSMovieAdapter(mYTSMovieArrayList);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mYTSMovieRecyclerView.setLayoutManager(mLinearLayoutManager);
        mYTSMovieRecyclerView.setAdapter(mMovieAdapter);

        mMovieAdapter.setCustomClickListener(new YTSMovieAdapter.CustomOnClickListener() {
            @Override
            public void onClick(View v, Object o) {
                MaterialDialog dialog = null;
                YTSMovie movie;
                YTSTorrent torrent = null;
                YTSTorrent[] torrents = null;
                switch (v.getId()) {
                    case R.id.cardChip3D:
                        movie = (YTSMovie) o;
                        torrents = movie.getTorrentArray();
                        int max = 0;
                        for (YTSTorrent tor : torrents) {
                            if (tor.getSize().toLowerCase().contains("GB")) {
                                int s = Integer.parseInt(tor.getSize().split(" ")[0].trim());
                                if (max > s) {
                                    max = s;
                                    torrent = tor;
                                }
                            }
                        }
                        dialog = new MaterialDialog.Builder(getActivity())
                                .title("3D")
                                .customView(R.layout.torrent_dialog_layout, true)
                                .build();
                        break;
                    case R.id.cardChip720p:
                        movie = (YTSMovie) o;
                        torrents = movie.getTorrentArray();
                        for (YTSTorrent tor : torrents) {
                            if (tor.getSize().toLowerCase().contains("mb")) {
                                torrent = tor;
                            }
                        }
                        dialog = new MaterialDialog.Builder(getActivity())
                                .title("720p")
                                .customView(R.layout.torrent_dialog_layout, true)
                                .build();
                        break;
                    case R.id.cardChip1080p:
                        movie = (YTSMovie) o;
                        torrents = movie.getTorrentArray();
                        for (YTSTorrent tor : torrents) {
                            Log.d("Torrent", tor.getSize());
                            if (tor.getSize().toLowerCase().contains("gb")) {
                                Log.d("Torrent", "GB Found");
                                torrent = tor;
                            }
                        }
                        dialog = new MaterialDialog.Builder(getActivity())
                                .title("1080p")
                                .customView(R.layout.torrent_dialog_layout, true)
                                .build();
                        break;
                }
                if (dialog != null) {
                    if (torrent != null) {
                        ClipboardManager mCManager
                                = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                        mCManager.setPrimaryClip(ClipData.newPlainText("TorrentURL", torrent.getUrl()));
                        Toast.makeText(getActivity(), "Torrent Copied to ClipBoard"
                                , Toast.LENGTH_SHORT).show();
                        ((TextView) dialog
                                .getCustomView()
                                .findViewById(R.id.seedsValueTextView)).setText(
                                String.valueOf(torrent.getSeeds())
                        );
                        ((TextView) dialog
                                .getCustomView()
                                .findViewById(R.id.peersValueTextView)).setText(
                                String.valueOf(torrent.getPeers())
                        );
                        ((TextView) dialog.getCustomView()
                                .findViewById(R.id.uploadDateTextView)).setText(torrent.getDate_upload());
                        ((TextView) dialog
                                .getCustomView()
                                .findViewById(R.id.fileSizeTextView)).setText(torrent.getSize());
                        dialog.show();
                    } else {
                        new MaterialDialog.Builder(getActivity())
                                .title("Torrent Not Found").build().show();
                    }
                }
            }
        });
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