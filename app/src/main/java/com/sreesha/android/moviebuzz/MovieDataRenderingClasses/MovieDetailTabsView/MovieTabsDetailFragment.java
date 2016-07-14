package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.AsyncMediaStorageClass;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.OnMoreReviewDataRequestedListener;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridFragment;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

import android.support.v4.content.CursorLoader;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

public class MovieTabsDetailFragment extends Fragment
        implements NestedScrollView.OnScrollChangeListener
        , View.OnClickListener
        , View.OnLongClickListener
        , MoviePosterGridActivity.DispatchMovieSelectionChange
        , OnMoreReviewDataRequestedListener
        , UIUpdatableInterface {
    Context mContext;
    MoviePosterGridFragment.NotifyMovieClick mNotifyMovieClickListener;

    ImageView mPosterImageView;
    ImageView mBackDropImageView;
    CollapsingToolbarLayout mCollapsingToolBar;

    AppBarLayout mAppBarLayoutBar;

    RatingBar favouritesRatingBar;
    ParcelableBitmap mBackDropBitmap;
    ParcelableBitmap mPosterBitmap;
    boolean isMovieClickDispatched = false;
    private String mPosterURL;
    private String mBackDropURL;
    private boolean mIsStateRestored = false;
    private final static String MOVIE_PARCELABLE_SAVED_KEY = "movieParcelableDetailViewFragmentKey";
    private static String mSharableYoutubeTrailerLink = null;
    TabLayout movieDetailTabLayout;
    ViewPager viewPager;
    ViewPager mBackDropViewPager;
    Adapter adapter;
    BackDropImagePagerAdapter mBackDropImagePagerAdapter;
    MovieDataInstance mMovieData;
    private final int FAVOURITES_LOADER_ID = 12762;
    private final int IS_FAVOURED_LOADER_ID = 12763;
    private final int MOVIE_DETAILED_DATA_LOADER_ID = 12764;
    private boolean isMovieFavoured = false;
    ReviewsFragment reviewFragment;
    MovieTrailersFragment trailersFragment;
    MovieDetailDisplayFragment movieDetailDisplayFragment;
    CastFragment mMovieCastFragment;
    CrewFragment mMovieCrewFragment;
    SimilarMoviesFragment mSimilarMoviesFragment;
    MoviePhotosFragment mMoviePhotosFragment;
    ImageView backdropImageView;

    String previousMovie = "previousMovie";
    String currentMovie = "currentMovie";
    ArrayList<Boolean> pageVisitedArrayList = new ArrayList<>(3);
    FloatingActionButton favouriteFloatingActionButton;
    FloatingActionButton shareFloatingActionButton;
    FloatingActionMenu floatingActionMenu;
    ArrayList<MovieImage> backDropImageList;

    CoordinatorLayout tabsCoordinatorLayout;

    public MovieTabsDetailFragment() {

    }

    public static MovieTabsDetailFragment newInstance(MovieDataInstance mMovieData) {
        MovieTabsDetailFragment fragment = new MovieTabsDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("TabsDebug", "TabsDetailFragment - OnAttach");
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("TabsDebug", "TabsDetailFragment - onActivityCreated");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TabsDebug", "TabsDetailFragment - onCreate");
        mMovieData = getArguments().getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
        if (savedInstanceState != null) {
            mMovieData = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("TabsDebug", "TabsDetailFragment - onSaveInstanceState");
        outState.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("TabsDebug", "TabsDetailFragment - onCreateView");
        View view = inflater.inflate(R.layout.fragment_movie_tabs_detail, container, false);
        setHasOptionsMenu(true);
        initializeViewElements(view);
        pageVisitedArrayList.add(0, false);
        pageVisitedArrayList.add(1, false);
        pageVisitedArrayList.add(2, false);
        pageVisitedArrayList.add(3, false);

        reviewFragment = ReviewsFragment.newInstance(null, null);
        reviewFragment.setMovieData(mMovieData);

        trailersFragment = MovieTrailersFragment.newInstance(null, null);
        trailersFragment.setMovieData(mMovieData);

        movieDetailDisplayFragment = MovieDetailDisplayFragment.newInstance(null, null);
        movieDetailDisplayFragment.setMovieData(mMovieData);

        mMovieCastFragment = CastFragment.newInstance(null, null);
        mMovieCastFragment.setMovieData(mMovieData);

        mMovieCrewFragment = CrewFragment.newInstance(null, null);
        mMovieCrewFragment.setMovieData(mMovieData);

        mMoviePhotosFragment = MoviePhotosFragment.newInstance(mMovieData);

        mSimilarMoviesFragment = SimilarMoviesFragment.newInstance(mMovieData);

        if (viewPager != null) {
            setupViewPager(viewPager);
            movieDetailTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            movieDetailTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            movieDetailTabLayout.setupWithViewPager(viewPager);
        }
        if (mMovieData != null) {
            Log.e("TabsDebug", "MovieDataUpdating");
            updateUIWithMovieData(mMovieData);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TabsDebug", "TabsDetailFragment - onResume");

    }

    private void initializeViewElements(View view) {
        //backdropImageView.setImageDrawable(getActivity().getDrawable(R.drawable.dark_night_sample_backdrop));
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mBackDropViewPager = (ViewPager) view.findViewById(R.id.backDropImagePager);
        movieDetailTabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mPosterImageView = (ImageView) view.findViewById(R.id.moviePosterImageView);

        mBackDropImageView = (ImageView) view.findViewById(R.id.backDropImageView);
        mCollapsingToolBar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolBar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mAppBarLayoutBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        tabsCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.tabsCoordinatorLayout);

        favouriteFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.favouriteFab);
        shareFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.shareFab);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.movieDetailFloatingActionMenu);
        favouriteFloatingActionButton.setOnClickListener(new FloatingActionMenu.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerFavouritesChanged();
            }
        });
        shareFloatingActionButton.setOnClickListener(new FloatingActionMenu.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieData != null) {
                    Log.e("TabsDebug", "INShareCardClick");
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, getFormattedMovieDataString());
                    intent.setType("text/plain");
                    startActivity(intent);
                }
            }
        });

    }

    public void triggerFavouritesChanged() {
        if (isMovieFavoured) {
            Log.e("TabsDebug", "INFavouritesCardClick");
            getActivity().getContentResolver()
                    .delete(MovieContract
                            .UserFavourite
                            .buildFavouritesUriWithMovieId(
                                    String.valueOf(mMovieData.getMovieID())
                            ), null, null);
            isMovieFavoured = false;
                    /*TODO:Update UI To show Un-Favoured Movie*/
            favouriteFloatingActionButton.setLabelText("Not Favoured");
            Toast.makeText(getActivity(), "Unfavourited", Toast.LENGTH_LONG).show();
        } else {
            ContentValues temp = new ContentValues();
            temp.put(MovieContract.UserFavourite.COLUMN_MOVIE_ID, mMovieData.getMovieID());
            getActivity().getContentResolver()
                    .insert(MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                            , temp);
            if (mPosterBitmap != null) {
                Log.e("StorageDebug", "RunningAsyncTask");
                runAsyncMediaStorage();
            }

            isMovieFavoured = true;
            favouriteFloatingActionButton.setLabelText("Favoured");
                    /*TODO:Update UI To show Favoured Movie*/
            Toast.makeText(getActivity(), "Favourited", Toast.LENGTH_LONG).show();
        }
    }

    private void setupBackDropViewPager(ViewPager mBackDropViewPager, String imageURL) {

        mBackDropViewPager.setOffscreenPageLimit(5);
        mBackDropImagePagerAdapter = new BackDropImagePagerAdapter(getChildFragmentManager());
        if (imageURL == null) {
            if (backDropImageList != null && backDropImageList.size() != 0) {
                ArrayList<MovieImageDisplayFragment> mImageFragmentArrayList = new ArrayList<>();
                for (MovieImage image : backDropImageList) {
                    mImageFragmentArrayList.add(MovieImageDisplayFragment.newInstance(image));
                }
                if (mImageFragmentArrayList.size() != 0) {
                    Log.e("MovieBackDropPager", "Setting Fragment List");
                    mBackDropImagePagerAdapter.setFragmentArrayList(mImageFragmentArrayList);
                }
                mBackDropImagePagerAdapter.notifyDataSetChanged();
            }
        } else {
            mBackDropImagePagerAdapter.addFragment(MovieImageDisplayFragment.newInstance(imageURL));
        }
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
                            movieDetailDisplayFragment.OnMovieDataChanged(mMovieData);
                        }
                        break;
                    case 1:
                        if (!pageVisitedArrayList.get(1)) {
                            pageVisitedArrayList.set(1, true);
                            reviewFragment.OnMovieDataChanged(mMovieData);
                        }
                        break;
                    case 2:
                        if (!pageVisitedArrayList.get(2)) {
                            pageVisitedArrayList.set(1, true);
                            trailersFragment.OnMovieDataChanged(mMovieData);
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

        adapter.addFragment(movieDetailDisplayFragment, "Movie");
        adapter.addFragment(reviewFragment, "Reviews");
        adapter.addFragment(trailersFragment, "Trailers");
        adapter.addFragment(mMovieCastFragment, "Cast");
        adapter.addFragment(mMovieCrewFragment, "Crew");
        adapter.addFragment(mSimilarMoviesFragment, "Similar Movies");
        adapter.addFragment(mMoviePhotosFragment, "Images");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onMovieSelectDispatched(MovieDataInstance instance) {
        mMovieData = instance;
        updateUIWithMovieData(instance);
        pageVisitedArrayList.set(0, false);
        pageVisitedArrayList.set(1, false);
        pageVisitedArrayList.set(2, false);
        try {
            if (!MoviePosterGridActivity.isInTwoPaneMode()) {
                if (movieDetailDisplayFragment != null) {
                    movieDetailDisplayFragment.onResume();
                }
                if (trailersFragment != null) {
                    trailersFragment.onResume();
                }
                if (reviewFragment != null) {
                    reviewFragment.onResume();
                }
                if (mMovieCastFragment != null) {
                    mMovieCastFragment.onResume();
                }
                if (mMovieCrewFragment != null) {
                    mMovieCrewFragment.onResume();
                }
                if (mSimilarMoviesFragment != null) {
                    mSimilarMoviesFragment.onResume();
                }
                if (mMoviePhotosFragment != null) {
                    mMoviePhotosFragment.onResume();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void updateUIWithMovieData(MovieDataInstance instance) {
        mMovieData = instance;
        mPosterURL = APIUrls.BASE_IMAGE_URL
                + "/"
                + APIUrls.API_IMG_W_500
                + mMovieData.getPOSTER_PATH();
        mBackDropURL = APIUrls.BASE_IMAGE_URL
                + "/"
                + APIUrls.API_IMG_W_500
                + mMovieData.getBackDropPath();
        Log.e("MovieBackDropPager", "Setting Up BackDropapgerWith URL");
        setupBackDropViewPager(mBackDropViewPager, mBackDropURL);
        if (!isMovieFavoured) {
            loadImageWithPicasso(mBackDropURL, mBackDropImageView, "backDrop");
            loadImageWithPicasso(mPosterURL, mPosterImageView, "poster");
            favouriteFloatingActionButton.setLabelText("Not Favoured");
        }
        if (mMovieData != null /*&& mIsStateRestored*/) {
            Log.e("TabsDebug", "Destroying and initializing loaders");
            destroyAllLoaders();
            getLoaderManager().initLoader(IS_FAVOURED_LOADER_ID, null, loaderCallBacks);
            getLoaderManager().initLoader(FAVOURITES_LOADER_ID, null, loaderCallBacks);
        }
    }


    private void destroyAllLoaders() {
        getLoaderManager().destroyLoader(IS_FAVOURED_LOADER_ID);
        getLoaderManager().destroyLoader(FAVOURITES_LOADER_ID);
    }

    private void loadImageWithPicasso(final String URL, final ImageView mImageView, final String imageType) {
        Target imageBitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBackDropBitmap = new ParcelableBitmap(bitmap);
                if (mImageView != null)
                    mImageView.setImageBitmap(bitmap);
                if (imageType.equals("poster")) {
                    mPosterBitmap = new ParcelableBitmap(bitmap);
                } else {
                    mBackDropBitmap = new ParcelableBitmap(bitmap);
                }
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        if (getActivity() != null) {
                            TabsFragmentColor.colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
                            TabsFragmentColor.colorPrimary = getResources().getColor(R.color.colorPrimary);
                            TabsFragmentColor.colorAccent = getResources().getColor(R.color.colorAccent);
                            TabsFragmentColor.colorVibrant = palette.getDarkVibrantColor(TabsFragmentColor.colorPrimary);
                            TabsFragmentColor.colorVibrantDark = palette.getDarkVibrantColor(TabsFragmentColor.colorPrimaryDark);
                            TabsFragmentColor.colorVibrantLight = palette.getDarkVibrantColor(TabsFragmentColor.colorPrimary);
                            TabsFragmentColor.colorMuted = palette.getMutedColor(TabsFragmentColor.colorPrimary);
                            TabsFragmentColor.colorMutedDark = palette.getDarkMutedColor(TabsFragmentColor.colorPrimaryDark);
                            TabsFragmentColor.colorMutedLight = palette.getMutedColor(TabsFragmentColor.colorPrimary);

                            //tabsCoordinatorLayout.setBackgroundColor(TabsFragmentColor.colorMutedLight);

                            mCollapsingToolBar.setContentScrimColor(TabsFragmentColor.colorVibrantDark);
                            mCollapsingToolBar
                                    .setStatusBarScrimColor(TabsFragmentColor.colorVibrantLight);

                       /* movieDetailTabLayout
                                .setTabTextColors(
                                        palette.getMutedColor(primary)
                                        , palette.getDarkVibrantColor(primaryDark)
                                );*/
                            favouriteFloatingActionButton.setColorNormal(TabsFragmentColor.colorMuted);
                            floatingActionMenu.setMenuButtonColorNormal(TabsFragmentColor.colorVibrantLight);
                            movieDetailTabLayout
                                    .setTabTextColors(
                                            getActivity().getResources().getColor(R.color.unSelected_white)
                                            , getActivity().getResources().getColor(R.color.md_white_1000)
                                    );
                            movieDetailTabLayout.setSelectedTabIndicatorColor(palette.getDarkVibrantColor(TabsFragmentColor.colorAccent));
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                //loadImageWithPicasso(URL, mImageView, imageType);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if (mImageView != null && mImageView.getTag() == null) {
            mImageView.setTag(imageBitmapTarget);
        }
        Picasso.with(getActivity()).load(URL)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_movie_white_48dp)
                .error(R.drawable.ic_error_white_48dp).into(imageBitmapTarget);
    }

    android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> loaderCallBacks
            = new android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mMovieData != null) {
                switch (id) {
                    case IS_FAVOURED_LOADER_ID:
                        Log.e("DatabaseDebug", "Loader Created");
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.UserFavourite.buildFavouritesUriWithMovieId(String.valueOf(mMovieData.getMovieID()))
                                , null
                                , null
                                , null
                                , null);
                    case FAVOURITES_LOADER_ID:
                        String sortOrder;
                        String sortPreference = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(getString(R.string.sort_options_list_key)
                                        , getString(R.string.sort_options_list_preference_default_value));
                        if (sortPreference.equals("vote_average.desc")) {
                            sortOrder = MovieContract.MovieData.COLUMN_VOTE_AVERAGE + " DESC";
                        } else {
                            sortOrder = MovieContract.MovieData.COLUMN_POPULARITY + " DESC";
                        }
                        return new CursorLoader(
                                getActivity()
                                , MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                                .buildUpon().appendPath(MovieContract.PATH_FAVOURITES_MOVIE_DATA).build()
                                , null
                                , null
                                , null
                                , sortOrder
                        );
                    default:
                        return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
            switch (loader.getId()) {
                case IS_FAVOURED_LOADER_ID:
                    Log.e("DatabaseDebug", "Load Finished");
                    //Log.e("DetailCursorDebug", "Size : " + data.getCount());
                    if (data.getCount() == 0) {
                        isMovieFavoured = false;
                        Log.e("DatabaseDebug", "Movie not found as favoured");
                        Toast.makeText(getActivity(), "Movie NOT Favoured", Toast.LENGTH_SHORT).show();
                    /*TODO:Update UI To Show That Movie is NOT Favoured*/
                        favouriteFloatingActionButton.setLabelText("Not Favoured");

                    } else {
                        Log.e("DatabaseDebug", "Movie found as favoured");
                        isMovieFavoured = true;
                        Toast.makeText(getActivity(), "Movie Favoured", Toast.LENGTH_SHORT).show();
                    /*TODO:Update UI To Show That Movie is Favoured*/
                        favouriteFloatingActionButton.setLabelText("Favoured");
                        runAsyncMediaRetrieval(mPosterImageView, mBackDropImageView);
                    }
                    break;
                case FAVOURITES_LOADER_ID:
                    if (data.getCount() == 0) {
                        Log.e("FavouritesDebug", "Error no Tuples Retrieved");
                    } else {
                        Log.e("FavouritesDebug", "\tTuples : " + data.getCount());
                    }

            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

        }
    };

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onMoreReviewDataRequested(int currentPage) {

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

    }

    @Override
    public void OnUIReadyToBeUpdated(String fragmentName) {
        switch (fragmentName) {
            case UIUpdatableInterface.REVIEWS_FRAGMENT:
                reviewFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.TRAILERS_FRAGMENT:
                trailersFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.MOVIE_DETAIL_FRAGMENT:
                Log.e("OnMovieDataChanged", "Calling OnMovieDataChanged");
                movieDetailDisplayFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.MOVIE_CAST_TABS_FRAGMENT:
                Log.e("OnMovieDataChanged", "Calling OnMovieDataChanged");
                mMovieCastFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.MOVIE_CREW_TABS_FRAGMENT:
                Log.e("OnMovieDataChanged", "Calling OnMovieDataChanged");

                mMovieCrewFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.SIMILAR_MOVIES_FRAGMENT:
                Log.e("OnMovieDataChanged", "Calling OnMovieDataChanged");

                mSimilarMoviesFragment.OnMovieDataChanged(mMovieData);
                break;
            case UIUpdatableInterface.MOVIE_PHOTOS_FRAGMENT:
                Log.e("OnMovieDataChanged", "Calling OnMovieDataChanged");

                mMoviePhotosFragment.OnMovieDataChanged(mMovieData);
                break;
        }
    }

    @Override
    public void OnMovieImageDataLoaded(ArrayList<MovieImage> mMovieImageArrayList) {
        this.backDropImageList = mMovieImageArrayList;
        if (mBackDropViewPager != null) {
            Log.e("MovieBackDropPager", "Setting Up BackDropPagerWith BackDropArrayList");
            setupBackDropViewPager(mBackDropViewPager, null);
        }
    }

    class BackDropImagePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<MovieImageDisplayFragment> imageFragmentArrayList = new ArrayList<>();

        public void addFragment(MovieImageDisplayFragment fragment) {
            imageFragmentArrayList.add(fragment);
        }

        public void setFragmentArrayList(ArrayList<MovieImageDisplayFragment> imageFragmentArrayList) {
            this.imageFragmentArrayList = imageFragmentArrayList;
        }

        public BackDropImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return imageFragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return imageFragmentArrayList.size();
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
            Log.e("TabsDebug", "Fragment Page - " + pageTitleArrayList.get(position));
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

    interface MovieTrailersFragmentInterface {
        void OnMovieDataChanged(MovieDataInstance movieDataInstance);
    }

    interface MovieReviewsFragmentInterface {
        void OnMovieDataChanged(MovieDataInstance movieDataInstance);
    }

    interface MovieDetailedDataFragmentInterface {
        void OnMovieDataChanged(MovieDataInstance reviewsInstance);
    }

    private void runAsyncMediaStorage() {
        new AsyncMediaStorageClass(
                AsyncMediaStorageClass.getFormattedPosterFileName(
                        mMovieData.getMovieID()
                        , AsyncMediaStorageClass.TYPE_POSTER
                )
                , AsyncMediaStorageClass.BITMAP_STORAGE_TASK
                , mPosterBitmap.getBitmap()
                , null
        ).setOnImageStoredListener(new AsyncMediaStorageClass.OnImageStoredListener() {
            @Override
            public void onImageStored(String errorMessage, String imagePath) {

            }
        }).execute();
        new AsyncMediaStorageClass(
                AsyncMediaStorageClass.getFormattedPosterFileName(
                        mMovieData.getMovieID()
                        , AsyncMediaStorageClass.TYPE_BACKDROP
                )
                , AsyncMediaStorageClass.BITMAP_STORAGE_TASK
                , mBackDropBitmap.getBitmap()
                , null
        ).setOnImageStoredListener(new AsyncMediaStorageClass.OnImageStoredListener() {
            @Override
            public void onImageStored(String errorMessage, String imagePath) {

            }
        }).execute();
    }

    private void runAsyncMediaRetrieval(ImageView posterImageView, ImageView backDropImageView) {
        new AsyncMediaStorageClass(
                AsyncMediaStorageClass.getFormattedPosterFileName(
                        mMovieData.getMovieID()
                        , AsyncMediaStorageClass.TYPE_BACKDROP
                )
                , AsyncMediaStorageClass.SET_IMAGE_VIEW_BITMAP_TASK
                , null
                , backDropImageView
        ).setOnBitmapRenderedListener(new AsyncMediaStorageClass.OnBitmapRenderedListener() {
            @Override
            public void onBitmapRendered(String errorMessage, Bitmap bitmap) {
                mBackDropImageView.setImageBitmap(bitmap);
            }
        }).execute();
    }

    public String getFormattedMovieDataString() {
        if (mSharableYoutubeTrailerLink != null) {
            return (
                    "**********"
                            + "\n" +
                            mMovieData.getOriginalTitle()
                            + "\n"
                            + "**********"
                            + "\n"
                            + "Rating : " + mMovieData.getAverageVoting()
                            + "\n"
                            + "Release Date : " + mMovieData.getReleaseDate()
                            + "\n"
                            + "OverView : " + mMovieData.getOverView()
                            + "\n"
                            + "Watch : " + mSharableYoutubeTrailerLink
            );
        } else {
            return (
                    "**********"
                            + "\n" +
                            mMovieData.getOriginalTitle()
                            + "\n"
                            + "**********"
                            + "\n"
                            + "Rating : " + mMovieData.getAverageVoting()
                            + "\n"
                            + "Release Date : " + mMovieData.getReleaseDate()
                            + "\n"
                            + "OverView : " + mMovieData.getOverView()
            );
        }
    }

    public boolean isMovieFavoured() {
        return isMovieFavoured;
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
