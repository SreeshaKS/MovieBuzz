package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.MovieTabsDetailActivity;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.NetworkConnectivityInfoDialogueFragment;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

public class DisplayDetailedMovieData extends AppCompatActivity
        implements NetworkConnectivityInfoDialogueFragment.ConfirmationDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_detailed_movie_data);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MovieDataInstance> parcelableIntentList = new ArrayList<MovieDataInstance>();
                parcelableIntentList.add(getDataFromCallingIntent());
                Intent temp =  new Intent(DisplayDetailedMovieData.this
                        , MovieTabsDetailActivity.class
                ).putParcelableArrayListExtra(getResources()
                        .getString(R.string.intent_movie_data_key), parcelableIntentList);
                startActivity(
                       temp
                );
            }
        });
        /*if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_frameLayout
                            , new DisplayExpandedMovieDataFragment(getDataFromCallingIntent())
                           *//* new MovieTabsDetailFragment(getDataFromCallingIntent())*//*
                            , "MovieDetailFragment")
                    .commit();
        }*/
    }

    public MovieDataInstance getDataFromCallingIntent() {
        try {
            Log.e("TabsDebug","MovieData retrieved from Intent");
            return ((MovieDataInstance) (
                    (ArrayList<Parcelable>) (
                            getIntent().getParcelableArrayListExtra(
                                    getResources()
                                            .getString(R.string.intent_movie_data_key)
                            )
                    )
            ).get(0));

        } catch (NullPointerException e) {
            Log.e("TabsDebug",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

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
