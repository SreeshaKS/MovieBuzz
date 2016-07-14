package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.NetworkConnectivityInfoDialogueFragment;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

public class MovieTabsDetailActivity extends AppCompatActivity
        implements NetworkConnectivityInfoDialogueFragment.ConfirmationDialogListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_tabs_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detailTabsViewContainer
                            ,MovieTabsDetailFragment.newInstance(getDataFromCallingIntent())
                            ,UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT)
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
}
