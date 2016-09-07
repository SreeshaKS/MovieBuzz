package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.NetworkConnectivityInfoDialogueFragment;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

import static com.sreesha.android.moviebuzz.R.id.toolbar;

public class MovieTabsDetailActivity extends AppCompatActivity
        implements NetworkConnectivityInfoDialogueFragment.ConfirmationDialogListener {
    MovieTabsDetailFragment mMovieTabsDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_tabs_detail);
        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mMovieTabsDetailFragment = MovieTabsDetailFragment.newInstance(getDataFromCallingIntent());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detailTabsViewContainer
                            , mMovieTabsDetailFragment
                            , UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT)
                    .commit();
        }
    }

    public MovieDataInstance getDataFromCallingIntent() {
        try {
            return ((MovieDataInstance) (
                    (ArrayList<Parcelable>) (
                            getIntent().getParcelableArrayListExtra(
                                    getResources()
                                            .getString(R.string.intent_movie_data_key)
                            )
                    )
            ).get(0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showNetworkConnectivityDialogue(String message) {
        //Create a Bundle to store the message
        Bundle messageBundle = new Bundle();
        messageBundle.putString("message", message);
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NetworkConnectivityInfoDialogueFragment();
        dialog.setArguments(messageBundle);
        dialog.show(getSupportFragmentManager()
                , "ConfirmationDialogFragment"
        );
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    interface NavItemClickDispatcher {
        void OnNavItemClicked(MenuItem item);
    }
}
