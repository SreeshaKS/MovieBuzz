package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncMovieSpecificsResults;
import com.sreesha.android.moviebuzz.Networking.CastDataInstance;
import com.sreesha.android.moviebuzz.Networking.CrewDataInstance;
import com.sreesha.android.moviebuzz.Networking.DownloadMovieSpecifics;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.Networking.PersonInstance;
import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PeopleProfileFragment extends Fragment {

    boolean mIsRestored = false;
    PersonInstance mPersonDataInstance;
    PopularPeopleInstance mPopPeopleDataInstance;
    public static final String PERSON_INSTANCE_DATA_KEY = "personDataKey";
    public static final String POPULAR_PERSON_INSTANCE_DATA_KEY = "popularPersonDataKey";
    DownloadMovieSpecifics mMovieSpecificsDownloadAsyncTask;
    ImageView mProfilePictureImageView;

    ArrayList<Boolean> pageVisitedArrayList = new ArrayList<>(3);

    TabLayout mPersonTabLayout;
    ViewPager viewPager;
    Adapter adapter;

    TextView biographyViewTextView;

    TextView mAgeTextView;
    TextView mPersonNameTextView;
    TextView mBirthdayTextView;
    TextView mPlaceOfBirthTextView;

    CoordinatorLayout mPersonProfileCoOrdLayout;

    public static PeopleProfileFragment getNewInstance(PopularPeopleInstance mPopularPeopleInstance) {
        PeopleProfileFragment mFragment = new PeopleProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(POPULAR_PERSON_INSTANCE_DATA_KEY, mPopularPeopleInstance);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPopPeopleDataInstance = getArguments().getParcelable(POPULAR_PERSON_INSTANCE_DATA_KEY);
        }
        if (savedInstanceState != null) {
            mIsRestored = true;
            mPersonDataInstance = savedInstanceState.getParcelable(PERSON_INSTANCE_DATA_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PERSON_INSTANCE_DATA_KEY, mPersonDataInstance);
        outState.putParcelable(POPULAR_PERSON_INSTANCE_DATA_KEY, mPopPeopleDataInstance);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_profile, container, false);
        initializeViewElements(view);

        pageVisitedArrayList.add(0, false);
        pageVisitedArrayList.add(1, false);
        pageVisitedArrayList.add(2, false);
        pageVisitedArrayList.add(3, false);
        createFragmentInstances();
        if (viewPager != null) {
            setupViewPager(viewPager);
            mPersonTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            mPersonTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            mPersonTabLayout.setupWithViewPager(viewPager);
        }
        return view;
    }

    private void createFragmentInstances() {

    }

    void updateUIWithPersonData() {
        Target mTarget =
                new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        if (bitmap != null) {
                            mProfilePictureImageView.setImageBitmap(bitmap);
                            Palette.PaletteAsyncListener asyncListener = new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    mPersonProfileCoOrdLayout
                                            .setBackgroundColor(
                                                    palette
                                                            .getDarkMutedColor(
                                                                    getActivity()
                                                                            .getResources()
                                                                            .getColor(R.color.colorPrimary)
                                                            )
                                            );
                                    /*holder.backGroundSheetCard
                                            .setCardBackgroundColor(
                                                    palette
                                                            .getLightMutedColor(
                                                                    getActivity()
                                                                            .getResources()
                                                                            .getColor(R.color.colorPrimary)
                                                            )
                                            );*/
                                }
                            };
                            Palette.from(bitmap).generate(asyncListener);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
        String URL = APIUrls.BASE_IMAGE_URL
                + "/" + APIUrls.API_IMG_W_342
                + mPersonDataInstance.getPROFILE_PATH();
        Picasso.with(getActivity())
                .load(URL)
                .into(mTarget);

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

    private void initializeViewElements(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mProfilePictureImageView = (ImageView) view.findViewById(R.id.profilePictureImageView);
        mPersonTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        biographyViewTextView = (TextView) view.findViewById(R.id.biographyTextView);
        mPersonProfileCoOrdLayout = (CoordinatorLayout) view.findViewById(R.id.personProfileCoOrdLayout);
        /*TextView mAgeTextView;
        TextView mPersonNameTextView;
        TextView mBirthdayTextView;*/
        mAgeTextView = (TextView) view.findViewById(R.id.ageTextView);
        mPersonNameTextView = (TextView) view.findViewById(R.id.personNameTextView);
        mBirthdayTextView = (TextView) view.findViewById(R.id.birthdayTextView);
        mPlaceOfBirthTextView = (TextView) view.findViewById(R.id.placeOfBirthTextView);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (!pageVisitedArrayList.get(0)) {
                            pageVisitedArrayList.set(0, true);
                        }
                        break;
                    case 1:
                        if (!pageVisitedArrayList.get(1)) {
                            pageVisitedArrayList.set(1, true);
                        }
                        break;
                    case 2:
                        if (!pageVisitedArrayList.get(2)) {
                            pageVisitedArrayList.set(1, true);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        adapter = new Adapter(getChildFragmentManager());
        /*TODO:Add FragmentClasses Here*/

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsRestored) {
            updateUIWithPersonData();
        } else {
            downloadPersonData();
        }
    }

    private void downloadPersonData() {
        mMovieSpecificsDownloadAsyncTask = new DownloadMovieSpecifics(
                getActivity()
                , new AsyncMovieSpecificsResults() {
            @Override
            protected void onResultJSON(JSONObject object) throws JSONException {

            }

            @Override
            protected void onResultString(String stringObject, String errorString, String parseStatus) {

            }

            @Override
            protected void onResultParsedIntoCastList(ArrayList<CastDataInstance> castList) {

            }

            @Override
            protected void onResultParsedIntoCrewList(ArrayList<CrewDataInstance> crewList) {

            }

            @Override
            protected void onResultParsedIntoPopularPersonInfo(
                    ArrayList<PopularPeopleInstance> popularPeopleInstanceArrayList
            ) {

            }

            @Override
            protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList, ArrayList<MovieImage> posterList) {

            }

            @Override
            protected void onResultParsedIntoPersonData(PersonInstance instance) {
                if (getActivity() != null) {
                    mPersonDataInstance = instance;
                    updateUIWithPersonData();
                }
            }
        });
        mMovieSpecificsDownloadAsyncTask.execute(
                APIUrls.buildPersonDetailsURL(mPopPeopleDataInstance.getID()).build().toString()
        );
    }

    static class Adapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragmentsArrayList = new ArrayList<>();
        ArrayList<String> pageTitleArrayList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String fragmentPageTitle) {
            fragmentsArrayList.add(fragment);
            pageTitleArrayList.add(fragmentPageTitle);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitleArrayList.get(position);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMovieSpecificsDownloadAsyncTask != null)
            if (!mMovieSpecificsDownloadAsyncTask.isCancelled()) {
                mMovieSpecificsDownloadAsyncTask.cancel(true);
            } else {
                mMovieSpecificsDownloadAsyncTask = null;
            }
    }
}
