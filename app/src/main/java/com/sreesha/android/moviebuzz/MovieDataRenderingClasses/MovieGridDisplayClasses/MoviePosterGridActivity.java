package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailActivity;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailFragment;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.UIUpdatableInterface;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay.PeopleDisplayActivity;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.NetworkConnectivityInfoDialogueFragment;
import com.sreesha.android.moviebuzz.Settings.AppInfoActivity;
import com.sreesha.android.moviebuzz.Settings.SettingsActivity;

import java.util.ArrayList;

import com.sreesha.android.moviebuzz.R;

public class MoviePosterGridActivity extends AppCompatActivity
        implements MoviePosterGridFragment.NotifyMovieClick
        , NetworkConnectivityInfoDialogueFragment.ConfirmationDialogListener {
    public static final String BOTTOM_SHEET_DIALOG_FRAGMENT_TAG = "bottomSheetDialogFragment";
    private static boolean sTwoPaneMode = false;

    private final static String TAG = MoviePosterGridActivity.class.getSimpleName();
    /*MoviePosterGridFragment mMoviePosterGridFragment;*/
    MovieTabsDashBoard mMovieTabsDashBoard;
    NavigationView mPosterGridNavigationView;
    DrawerLayout mPosterGridDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;

    private BottomSheetBehavior mBottomSheetBehaviour;
    private BottomSheetSearchReveal mBottomSheetSearchReveal;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_poster_grid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Popular Movies");

        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
/*Fragment ID moviePosterGridFragmentContainer*/
       /* mMoviePosterGridFragment = (MoviePosterGridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.moviePosterGridFragmentContainer);
        initNavigationView(toolbar);
        */
        mMovieTabsDashBoard = (MovieTabsDashBoard) getSupportFragmentManager()
                .findFragmentById(R.id.moviePosterGridFragmentContainer);
        mBottomSheetSearchReveal = new BottomSheetSearchReveal();

        initNavigationView(toolbar);

        if (findViewById(R.id.detail_fragment_frameLayout) != null) {
            sTwoPaneMode = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment_frameLayout
                                , MovieTabsDetailFragment.newInstance(null)
                                , UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT)
                        .commit();
            }
        } else {
            sTwoPaneMode = false;
        }
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }

    private void initNavigationView(Toolbar toolbar) {
        mPosterGridNavigationView = (NavigationView) findViewById(R.id.posterGirdNavigationView);
        mPosterGridDrawerLayout = (DrawerLayout) findViewById(R.id.posterGridDrawerLayout);

        mPosterGridNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.highestRatedMoviesNavMenu:
                    case R.id.mostPopularMoviesNavMenu:
                    case R.id.upComingMoviesNavMenu:
                    case R.id.favouriteMoviesNavMenu:
                    case R.id.nowPlayingMoviesNavMenu:
                        mMovieTabsDashBoard.OnNavigationItemClicked(item.getItemId());
                        break;
                    case R.id.settingsNavMenu:
                        mPosterGridDrawerLayout.closeDrawers();
                        startActivity(new Intent(MoviePosterGridActivity.this, SettingsActivity.class));
                        break;
                    case R.id.popularPeopleNavMenu:
                        mPosterGridDrawerLayout.closeDrawers();
                        startActivity(new Intent(MoviePosterGridActivity.this, PeopleDisplayActivity.class));
                        break;
                }
                mPosterGridDrawerLayout.closeDrawers();
                return false;
            }
        });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this
                , mPosterGridDrawerLayout
                , toolbar
                , R.string.posterGridDrawer_close
                , R.string.posterGridDrawer_open) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mPosterGridDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mPosterGridDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            mPosterGridDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mPosterGridDrawerLayout != null && mPosterGridDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.home:
                mPosterGridDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.aboutApp:
                startActivity(new Intent(MoviePosterGridActivity.this, AppInfoActivity.class));
                return true;
            case R.id.followOnTwitter:
                return true;
            case R.id.sendFeedBack:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kssreesha@gmail.com"});
                startActivity(emailIntent);
                return true;
            case R.id.search:
                mBottomSheetSearchReveal = BottomSheetSearchReveal.newInstance(null, null);
                mBottomSheetSearchReveal.show(getSupportFragmentManager(),BOTTOM_SHEET_DIALOG_FRAGMENT_TAG );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_poster_grid, menu);


       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && *//*mMoviePosterGridFragment != null*//*mMovieTabsDashBoard != null) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        }*/
        return true;
    }

    @Override
    public void onMovieClicked(ArrayList<MovieDataInstance> parcelableIntentMovieList, MovieDataInstance instance) {
        Log.d("SimClick", "Click Dispatched");
        View fragmentView = findViewById(R.id.detail_fragment_frameLayout);
        if (sTwoPaneMode) {
            CustomAnimator.createCircularRevealEffect(
                    fragmentView
                    , fragmentView.getWidth()
                    , fragmentView.getHeight()
                    , 100
                    , 1000
            );
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_frameLayout
                            , MovieTabsDetailFragment.newInstance(instance)
                            , UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT)
                    .commit();
        } else {
            startActivity(new Intent(this, MovieTabsDetailActivity.class)
                    .putParcelableArrayListExtra(getString(R.string.intent_movie_data_key), parcelableIntentMovieList));
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    public interface DispatchMovieSelectionChange {
        void onMovieSelectDispatched(MovieDataInstance instance);
    }

    public static boolean isInTwoPaneMode() {
        return sTwoPaneMode;
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
}
