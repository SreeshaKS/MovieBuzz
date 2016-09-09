package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.DownloadMovieSpecifics;
import com.sreesha.android.moviebuzz.Networking.PersonInstance;
import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

public class PersonBioFragment extends Fragment {
    boolean mIsRestored = false;
    PersonInstance mPersonDataInstance;
    PopularPeopleInstance mPopPeopleDataInstance;

    DownloadMovieSpecifics mMovieSpecificsDownloadAsyncTask;

    TextView biographyViewTextView;

    TextView mAgeTextView;
    TextView mPersonNameTextView;
    TextView mBirthdayTextView;
    TextView mPlaceOfBirthTextView;

    public PersonBioFragment() {
        // Required empty public constructor
    }

    public static PersonBioFragment newInstance(PopularPeopleInstance mPopularPeopleInstance
            , PersonInstance mPersonDataInstance) {
        PersonBioFragment fragment = new PersonBioFragment();
        Bundle args = new Bundle();
        //TODO:Add Fragment Arguments Here
        args.putParcelable(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY, mPopularPeopleInstance);
        args.putParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY, mPersonDataInstance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsRestored = true;
            mPersonDataInstance = savedInstanceState.getParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY);
            mPersonDataInstance = savedInstanceState.getParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY);
        } else if (getArguments() != null) {
            mPopPeopleDataInstance = getArguments().getParcelable(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY);
            mPersonDataInstance = getArguments().getParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PeopleProfileFragment.PERSON_INSTANCE_DATA_KEY, mPersonDataInstance);
        outState.putParcelable(PeopleProfileFragment.POPULAR_PERSON_INSTANCE_DATA_KEY, mPopPeopleDataInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_bio, container, false);
        initializeViewElements(view);
        return view;
    }

    private void initializeViewElements(View view) {
        biographyViewTextView = (TextView) view.findViewById(R.id.biographyTextView);
        mAgeTextView = (TextView) view.findViewById(R.id.ageTextView);
        mPersonNameTextView = (TextView) view.findViewById(R.id.personNameTextView);
        mBirthdayTextView = (TextView) view.findViewById(R.id.birthdayTextView);
        mPlaceOfBirthTextView = (TextView) view.findViewById(R.id.placeOfBirthTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUIWithPersonData();
    }

    void updateUIWithPersonData() {
        if (PeopleProfileFragment
                .ProfilePaletteColors.isPaletteSet) {
            int bodyTextColor
                    = PeopleProfileFragment
                    .ProfilePaletteColors
                    .mLightMutedSwatch.getBodyTextColor();
            int titleTextColor
                    = PeopleProfileFragment
                    .ProfilePaletteColors
                    .mLightMutedSwatch.getTitleTextColor();
            biographyViewTextView.setTextColor(
                    bodyTextColor
            );
            mPersonNameTextView
                    .setTextColor(titleTextColor);
            mPlaceOfBirthTextView
                    .setTextColor(titleTextColor);
            mBirthdayTextView
                    .setTextColor(titleTextColor);
            mAgeTextView
                    .setTextColor(titleTextColor);
        }
        biographyViewTextView.setText(mPersonDataInstance.getBIOGRAPHY());
        mPersonNameTextView.setText(mPersonDataInstance.getNAME());
        if ((mPersonDataInstance.getPLACE_OF_BIRTH()) == null
                ||
                (mPersonDataInstance.getPLACE_OF_BIRTH()).equals("null")) {
            mPlaceOfBirthTextView.setText(
                    "Not Available"
            );
        } else {
            mPlaceOfBirthTextView.setText(
                    getActivity().getString(R.string.place_birth_pholder_text)
                            + mPersonDataInstance.getPLACE_OF_BIRTH()
            );
        }

        mBirthdayTextView.setText(getActivity()
                .getString(R.string.birthday_placeholder_text)
                + mPersonDataInstance.getBIRTHDAY()
        );
        //TODO:Convert Given date fo birth to age
        mAgeTextView.setVisibility(View.GONE);
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
