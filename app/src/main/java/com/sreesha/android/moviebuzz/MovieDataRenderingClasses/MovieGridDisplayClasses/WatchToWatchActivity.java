package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.R;

public class WatchToWatchActivity extends AppCompatActivity {
    ToWatchFragment mToWatchFragment;
    public static final String TO_WATCH_FRAGMENT_TAG = "toWatchFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_to_watch);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mToWatchFragment = ToWatchFragment.newInstance(handleIntent());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.toWatchFragmentContainer, mToWatchFragment, TO_WATCH_FRAGMENT_TAG)
                    .commit();
        }
    }

    private int handleIntent() {
        return getIntent().getIntExtra(MovieContract.UserFavourite.COLUMN_MOVIE_TYPE,
                MovieContract.UserFavourite.TO_WATCH_MOVIE_TYPE);

    }

}
