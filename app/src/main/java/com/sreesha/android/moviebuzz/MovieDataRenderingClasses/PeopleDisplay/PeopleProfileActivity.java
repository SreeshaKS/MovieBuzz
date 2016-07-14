package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

public class PeopleProfileActivity extends AppCompatActivity {
    PeopleProfileFragment mPeopleProfileFragment;
    PopularPeopleInstance mPopPeopleInstance;
    public static final String PEOPLE_PROFILE_FRAGMENT_TAG = "peopleProfileFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_people_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            handleIntent();
            mPeopleProfileFragment = PeopleProfileFragment.getNewInstance(mPopPeopleInstance);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.peopleProfileFragmentContainer
                            , mPeopleProfileFragment
                            , PEOPLE_PROFILE_FRAGMENT_TAG
                    )
                    .commit();
        }
    }

    void handleIntent() {
        if (getIntent() != null) {
            Bundle args = getIntent().getBundleExtra(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY);
            mPopPeopleInstance = args.getParcelable(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY);
        }
    }
}
