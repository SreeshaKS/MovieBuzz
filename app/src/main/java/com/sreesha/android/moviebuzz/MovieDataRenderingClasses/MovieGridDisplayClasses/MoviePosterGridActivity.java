package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.FireBaseUI.MovieBuzzReviewsActivity;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailActivity;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailFragment;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.UIUpdatableInterface;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay.PeopleDisplayActivity;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.NetworkConnectivityInfoDialogueFragment;
import com.sreesha.android.moviebuzz.Settings.AppInfoActivity;
import com.sreesha.android.moviebuzz.Settings.LoginActivity;
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

    FirebaseUser user;

    CardView mProfileImageBackgroundCV;
    CardView mHeaderCardView;
    TextView mUserNameTextView;
    TextView mEmailTextView;
    ImageView mProfileImageView;
    View headerView;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("LangPreferences", preferences.getString(
                getString(R.string.preferences_user_language_key)
                , "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_poster_grid);

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);

        initializeViewElements();


        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        mMovieTabsDashBoard = (MovieTabsDashBoard) getSupportFragmentManager()
                .findFragmentById(R.id.moviePosterGridFragmentContainer);
        mBottomSheetSearchReveal = new BottomSheetSearchReveal();


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
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateLoginHeaderInfo(true);
        } else {
            updateLoginHeaderInfo(false);
        }
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }

    private void initializeViewElements() {
        Log.d("UriTrailers", ContentUris.withAppendedId(MovieContract.MovieTrailers.TRAILER_CONTENT_URI, 23).toString());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Popular Movies");

        initNavigationView(toolbar);

        mProfileImageBackgroundCV = (CardView) headerView.findViewById(R.id.profileImageBackgroundCV);
        mHeaderCardView = (CardView) headerView.findViewById(R.id.headerCardView);
        mUserNameTextView = (TextView) headerView.findViewById(R.id.userNameTextView);
        mEmailTextView = (TextView) headerView.findViewById(R.id.emailTextView);
        mProfileImageView = (ImageView) headerView.findViewById(R.id.profileImageView);
    }

    void updateLoginHeaderInfo(boolean isLoggedIN) {
        Target imageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mProfileImageView.setImageBitmap(bitmap);
                Palette.PaletteAsyncListener asyncListener = new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        mHeaderCardView
                                .setCardBackgroundColor(
                                        palette
                                                .getVibrantColor(
                                                        getResources()
                                                                .getColor(R.color.colorPrimary)
                                                )
                                );
                        mProfileImageBackgroundCV
                                .setCardBackgroundColor(
                                        palette
                                                .getLightMutedColor(
                                                        getResources()
                                                                .getColor(R.color.colorPrimary)
                                                )
                                );
                    }
                };
                Palette.from(bitmap).generate(asyncListener);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if (isLoggedIN) {
            mUserNameTextView.setText(user.getDisplayName());
            mEmailTextView.setText(user.getEmail());
            Picasso
                    .with(getApplicationContext())
                    .load(user.getPhotoUrl())
                    .into(imageTarget);
        } else {
            mUserNameTextView.setVisibility(View.GONE);
            mEmailTextView.setVisibility(View.GONE);
            Picasso
                    .with(getApplicationContext())
                    .load(R.drawable.movie_app_icon_green)
                    .into(imageTarget);
        }
    }

    private void initNavigationView(Toolbar toolbar) {
        mPosterGridNavigationView = (NavigationView) findViewById(R.id.posterGirdNavigationView);
        mPosterGridDrawerLayout = (DrawerLayout) findViewById(R.id.posterGridDrawerLayout);
        headerView = mPosterGridNavigationView.getHeaderView(0);

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
                    case R.id.toWatchNavMenu:
                        mPosterGridDrawerLayout.closeDrawers();
                        Intent mIntent1 = new Intent(MoviePosterGridActivity.this,
                                WatchToWatchActivity.class);
                        mIntent1.putExtra(MovieContract.UserFavourite.COLUMN_MOVIE_TYPE
                                , MovieContract.UserFavourite.TO_WATCH_MOVIE_TYPE);
                        startActivity(mIntent1);
                        break;
                    case R.id.watchedNavMenu:
                        mPosterGridDrawerLayout.closeDrawers();
                        Intent mIntent2 = new Intent(MoviePosterGridActivity.this,
                                WatchToWatchActivity.class);
                        mIntent2.putExtra(MovieContract.UserFavourite.COLUMN_MOVIE_TYPE
                                , MovieContract.UserFavourite.WATCHED_MOVIE_TYPE);
                        startActivity(mIntent2);
                        break;
                    case R.id.movieBuzzReviewsNavMenu:
                        startActivity(new Intent(MoviePosterGridActivity.this,
                                MovieBuzzReviewsActivity.class));
                        return true;
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
                Log.d("Drawer Debug", "Drawer Closed");
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d("Drawer Debug", "Drawer Closed");
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
                mBottomSheetSearchReveal.show(getSupportFragmentManager(), BOTTOM_SHEET_DIALOG_FRAGMENT_TAG);
                return true;
            case R.id.login:
                startActivity(new Intent(MoviePosterGridActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.rateThisAppONStore:
                goToPlayStore();
                return true;
            case R.id.likeFaceBookPage:
                startActivity(newFacebookIntent(getPackageManager(), APIUrls.FACEBOOK_URL));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri;
        try {
            pm.getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public void goToPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
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
