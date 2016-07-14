package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

public class PeopleDisplayActivity extends AppCompatActivity implements PersonClickListener {

    PeopleProfileFragment mPeopleProfileFragment;
    PopularPeopleFragment mPopularPeopleFragment;
    boolean mTwoPane = false;
    FrameLayout mPeopleDisplayFrameLayout;
    PopularPeopleInstance mPopPeopleInstance;
    RelativeLayout mPopularListLayoutWrapper;
    RelativeLayout mPeopleProfileLayoutWrapper;
    boolean mIsStateRestored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_people_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            mIsStateRestored = true;
        }
        mPeopleDisplayFrameLayout = (FrameLayout) findViewById(R.id.peopleDisplayFrameLayout);
        mPopularListLayoutWrapper = (RelativeLayout) findViewById(R.id.popularListLayoutWrapper);
        mPeopleProfileLayoutWrapper = (RelativeLayout) findViewById(R.id.peopleProfileLayoutWrapper);
        if (mTwoPane) {

        } else {
        }
    }

    @Override
    public void onPersonClicked(PopularPeopleInstance instance) {
        if (mTwoPane) {
            //TODO:Integrate TwoPane mode
        } else {
            ObjectAnimator mAnimatorY = ObjectAnimator.ofFloat(
                    mPopularListLayoutWrapper
                    , "translationY",
                    0, -mPeopleDisplayFrameLayout.getHeight());
            mAnimatorY.setDuration(500);
            mAnimatorY.setInterpolator(new AccelerateInterpolator(2.2f));
            mAnimatorY.start();

            Intent intent = new Intent(PeopleDisplayActivity.this, PeopleProfileActivity.class);
            Bundle args = new Bundle();
            args.putParcelable(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY, instance);
            intent.putExtra(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY, args);
            startActivity(intent);

            finish();
        }
    }
}
