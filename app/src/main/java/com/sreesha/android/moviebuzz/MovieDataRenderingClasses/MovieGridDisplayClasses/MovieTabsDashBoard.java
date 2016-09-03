package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

public class MovieTabsDashBoard extends Fragment implements DispatchNavigationInterface

        , AsyncSearchTask.SearchResultDispatchInterface
        , MoviePosterGridFragment.NotifyMovieClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TabLayout mMovieDetailTabLayout;
    ViewPager mViewPager;
    Adapter mAdapter;
    MovieDataInstance mMovieData;
    ArrayList<Boolean> mPageVisitedArrayList = new ArrayList<>(3);
    MoviePosterGridFragment mMoviePosterGridFragment;
    UpComingMoviesFragment mUpcomingMoviesFragment;
    PlayingNowMoviesFragment mPlayingNowMoviesFragment;
    HighestRatedMoviesFragment mHighestRatedMvoiesFragment;
    FavouriteMoviesFragment mFavouriteMoviesFragment;



    private int[] tabIcons = {
            R.drawable.ic_favorite_black_48dp
            , R.drawable.ic_av_timer_black_48dp
            , R.drawable.ic_play_circle_outline_black_48dp
            , R.drawable.ic_star_black_48dp
    };

    public MovieTabsDashBoard() {
        // Required empty public constructor
    }

    public static MovieTabsDashBoard newInstance(String param1, String param2) {
        MovieTabsDashBoard fragment = new MovieTabsDashBoard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_tabs_dash_board, container, false);
        setHasOptionsMenu(true);
        initializeViewElements(view);

        mMoviePosterGridFragment = MoviePosterGridFragment.newInstance();
        mUpcomingMoviesFragment = UpComingMoviesFragment.newInstance(null, null);
        mPlayingNowMoviesFragment = PlayingNowMoviesFragment.newInstance(null, null);
        mHighestRatedMvoiesFragment = HighestRatedMoviesFragment.newInstance(null, null);
        mFavouriteMoviesFragment = FavouriteMoviesFragment.newInstance(null, null);

        mPageVisitedArrayList.add(0, false);
        mPageVisitedArrayList.add(1, false);
        mPageVisitedArrayList.add(2, false);
        mPageVisitedArrayList.add(3, false);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            mMovieDetailTabLayout = (TabLayout) view.findViewById(R.id.tabs);
            mMovieDetailTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            mMovieDetailTabLayout.setupWithViewPager(mViewPager);

            mMovieDetailTabLayout.getTabAt(0).setIcon(R.drawable.ic_favorite_white_48dp);
            mMovieDetailTabLayout.getTabAt(1).setIcon(R.drawable.ic_av_timer_white_48dp);
            mMovieDetailTabLayout.getTabAt(2).setIcon(R.drawable.ic_play_circle_filled_white_48dp);
            mMovieDetailTabLayout.getTabAt(3).setIcon(R.drawable.ic_stars_white_48dp);
            mMovieDetailTabLayout.getTabAt(4).setIcon(R.drawable.ic_star_white_48dp);

        }

        return view;
    }


    private void initializeViewElements(View view) {
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        getActivity().setTitle(getActivity().getString(R.string.title_popular_movies_string));
                        if (!mPageVisitedArrayList.get(0)) {
                            mPageVisitedArrayList.set(0, true);
                            //TODO:Perform FragmentUI Update
                        }
                        break;
                    case 1:
                        getActivity().setTitle(getActivity().getString(R.string.title_upcoming_movies_string));
                        if (!mPageVisitedArrayList.get(1)) {
                            mPageVisitedArrayList.set(1, true);
                            //TODO:Perform FragmentUI Update

                        }
                        break;
                    case 2:
                        getActivity().setTitle(getActivity().getString(R.string.title_now_playing_string));
                        if (!mPageVisitedArrayList.get(2)) {
                            mPageVisitedArrayList.set(1, true);
                            //TODO:Perform FragmentUI Update
                        }
                        break;
                    case 3:
                        getActivity().setTitle(getActivity().getString(R.string.title_highest_rated_movies_string));
                        if (!mPageVisitedArrayList.get(3)) {
                            mPageVisitedArrayList.set(1, true);
                            //TODO:Perform FragmentUI Update
                        }
                        break;
                    case 4:
                        getActivity().setTitle(getActivity().getString(R.string.title_favourites_string));
                        if (mAdapter.getItem(4) != null) {
                            //Call OnResume to reload the Favourites Data
                            mAdapter.getItem(position).onResume();
                        }
                        //mAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mAdapter = new Adapter(getChildFragmentManager());
        /*TODO:Add FragmentClasses Here*/
        mAdapter.addFragment(mMoviePosterGridFragment, "");
        mAdapter.addFragment(mUpcomingMoviesFragment, "");
        mAdapter.addFragment(mPlayingNowMoviesFragment, "");
        mAdapter.addFragment(mHighestRatedMvoiesFragment, "");
        mAdapter.addFragment(mFavouriteMoviesFragment, "");
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void OnNavigationItemClicked(int menuItemID) {
        switch (menuItemID) {
            case R.id.highestRatedMoviesNavMenu:
                //TODO:Add Animated Scroll code to scroll to the correct position
                mMovieDetailTabLayout.getTabAt(3).select();
                break;
            case R.id.mostPopularMoviesNavMenu:
                //TODO:Add Animated Scroll code to scroll to the correct position
                mMovieDetailTabLayout.getTabAt(0).select();
                break;
            case R.id.upComingMoviesNavMenu:
                //TODO:Add Animated Scroll code to scroll to the correct position
                mMovieDetailTabLayout.getTabAt(1).select();
                break;
            case R.id.nowPlayingMoviesNavMenu:
                //TODO:Add Animated Scroll code to scroll to the correct position
                mMovieDetailTabLayout.getTabAt(2).select();
                break;
            case R.id.favouriteMoviesNavMenu:
                //TODO:Add Animated Scroll code to scroll to the correct position
                mMovieDetailTabLayout.getTabAt(4).select();
                break;
        }
    }

    @Override
    public void onSearchResultAcquired(MovieDataInstance instance) {
        mMoviePosterGridFragment.onSearchResultAcquired(instance);
    }

    public void dispatchQueryResult(Cursor queryResultCursor, String queryMovieTitleString) {
        if (mMoviePosterGridFragment != null)
            mMoviePosterGridFragment.dispatchQueryResult(queryResultCursor, queryMovieTitleString);
    }

    @Override
    public void onMovieClicked(ArrayList<MovieDataInstance> parcelableMovieList, MovieDataInstance instance) {
        if (getActivity() != null) {
            ((MoviePosterGridActivity) getActivity())
                    .onMovieClicked(parcelableMovieList, instance);
        }
    }

    class Adapter extends FragmentStatePagerAdapter {
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class TabsFragmentColor {
        public static int colorPrimary;
        public static int colorPrimaryDark;
        public static int colorAccent;
        public static int colorVibrant;
        public static int colorVibrantDark;
        public static int colorVibrantLight;
        public static int colorMuted;
        public static int colorMutedDark;
        public static int colorMutedLight;
    }
}
