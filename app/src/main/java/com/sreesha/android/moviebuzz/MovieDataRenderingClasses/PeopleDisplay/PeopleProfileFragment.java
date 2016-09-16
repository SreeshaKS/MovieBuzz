package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.sreesha.android.moviebuzz.Networking.Utility;
import com.sreesha.android.moviebuzz.R;
import com.sreesha.android.moviebuzz.Settings.MovieBuzzApplication;

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
    CollapsingToolbarLayout mCollapsingToolBarLayout;

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

    PersonBioFragment mPersonBioFragment;
    PersonImagesFragment mPersonImageFragment;

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
                                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                                    Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                    if (vibrantSwatch != null
                                            && darkVibrantSwatch != null) {
                                        /*//Static class for marshalling Palette Generated colors and swatches
                                        ProfilePaletteColors
                                                .parsePalette(palette);

                                        mPersonTabLayout
                                                .setTabTextColors(
                                                        ProfilePaletteColors.mLightVibrantSwatch
                                                                .getTitleTextColor()
                                                        , ProfilePaletteColors.mDarkVibrantSwatch
                                                                .getTitleTextColor()
                                                );*/
                                    }
                                    mPersonTabLayout
                                            .setSelectedTabIndicatorColor(
                                                    ProfilePaletteColors.mutedDark
                                            );
                                    mPersonProfileCoOrdLayout
                                            .setBackgroundColor(
                                                    ProfilePaletteColors.mutedDark
                                            );
                                    mCollapsingToolBarLayout
                                            .setContentScrimColor(
                                                    ProfilePaletteColors.mutedDark
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
        addFragmentInstancesToViewPager();
    }

    private void initializeViewElements(View view) {
        mCollapsingToolBarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.pPCollapsingToolBarLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mProfilePictureImageView = (ImageView) view.findViewById(R.id.profilePictureImageView);
        mPersonTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mPersonTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        biographyViewTextView = (TextView) view.findViewById(R.id.biographyTextView);
        mPersonProfileCoOrdLayout = (CoordinatorLayout) view.findViewById(R.id.personProfileCoOrdLayout);
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
        if (Utility.isConnectedToNetwork(getActivity())) {
            mMovieSpecificsDownloadAsyncTask = new DownloadMovieSpecifics(
                    getActivity()
                    , new AsyncMovieSpecificsResults() {
                @Override
                protected void onResultJSON(JSONObject object) throws JSONException {

                }

                @Override
                protected void onResultString(String stringObject, String errorString, String parseStatus) {
                    showNetworkConnectionErrorDialog();
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
                protected void onResultParsedIntoMovieImages(ArrayList<MovieImage> backDropsList, ArrayList<MovieImage> posterList, ArrayList<PersonImage> personImageList) {

                }

                @Override
                protected void onResultParsedIntoPersonData(PersonInstance instance) {
                    if (getActivity() != null && getActivity() instanceof PeopleProfileActivity) {
                        mPersonDataInstance = instance;
                        updateUIWithPersonData();
                    }
                }
            });
            mMovieSpecificsDownloadAsyncTask.execute(
                    APIUrls.buildPersonDetailsURL(mPopPeopleDataInstance.getID()).build().toString()
            );
        } else {
            showNetworkConnectionErrorDialog();
        }
    }

    void showNetworkConnectionErrorDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .content(R.string.please_connect_to_a_working_internet_connection_string)
                .positiveText(R.string.R_string_tryagain)
                .negativeText(R.string.cancel_go_back)
                .build();
        dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPersonData();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getActivity().finish();
            }
        });
        dialog.show();
    }

    void addFragmentInstancesToViewPager() {
        if (viewPager != null && adapter != null && adapter.getCount() == 0) {

            mPersonBioFragment = PersonBioFragment.newInstance(mPopPeopleDataInstance
                    , mPersonDataInstance);
            mPersonImageFragment =
                    PersonImagesFragment.newInstance(
                            mPersonDataInstance
                    );
            adapter.addFragment(mPersonBioFragment
                    , getActivity()
                            .getString(R.string.fragment_person_bio_title_string)
            );
            adapter.addFragment(mPersonImageFragment
                    , getActivity().getString(R.string.fragment_person_images_string));
            adapter.notifyDataSetChanged();
        }
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

    static class ProfilePaletteColors {
        static boolean isPaletteSet = false;
        static Palette palette;
        static Palette.Swatch mVibrantSwatch;
        static Palette.Swatch mDarkVibrantSwatch;
        static Palette.Swatch mLightVibrantSwatch;
        static Palette.Swatch mMutedSwatch;
        static Palette.Swatch mDarkMutedSwatch;
        static Palette.Swatch mLightMutedSwatch;
        static int vibrant;
        static int vibrantLight;
        static int vibrantDark;
        static int muted;
        static int mutedLight;
        static int mutedDark;
        static int primaryColor =
                MovieBuzzApplication
                        .getAppContext()
                        .getResources()
                        .getColor(R.color.colorPrimary);

        public static void setDefaults() {
            isPaletteSet = false;
            vibrant
                    = vibrantLight
                    = vibrantDark
                    = muted
                    = mutedDark
                    = mutedLight
                    = primaryColor;
        }

        public static void parsePalette(Palette palette) {
            isPaletteSet = true;
            ProfilePaletteColors.palette = palette;
            mVibrantSwatch = palette.getVibrantSwatch();
            mDarkVibrantSwatch = palette.getDarkVibrantSwatch();
            mLightVibrantSwatch = palette.getLightVibrantSwatch();
            mMutedSwatch = palette.getMutedSwatch();
            mDarkMutedSwatch = palette.getDarkMutedSwatch();
            mLightMutedSwatch = palette.getLightMutedSwatch();
            vibrant = palette.getVibrantColor(primaryColor);
            vibrantLight = palette.getLightVibrantColor(primaryColor);
            vibrantDark = palette.getDarkVibrantColor(primaryColor);
            muted = palette.getMutedColor(primaryColor);
            mutedLight = palette.getLightMutedColor(primaryColor);
            mutedDark = palette.getDarkMutedColor(primaryColor);
        }
    }
}
