package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.DataHandlerClasses.CursorRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView.ParcelableBitmap;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.*;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.AsyncResult;
import com.sreesha.android.moviebuzz.Networking.DownloadData;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.M)
public class DisplayExpandedMovieDataFragment extends Fragment
        implements NestedScrollView.OnScrollChangeListener
        , View.OnClickListener
        , View.OnLongClickListener
        , MoviePosterGridActivity.DispatchMovieSelectionChange
        , LoaderManager.LoaderCallbacks<Cursor>
        , OnMoreReviewDataRequestedListener {

    Context mContext;

    MovieDataInstance mMovieData = null;
    ImageView mPosterImageView;
    ImageView mBackDropImageView;
    CollapsingToolbarLayout mCollapsingToolBar;
    NestedScrollView mContentNestedScrollView;
    CardView posterCardView;
    CardView mShareCard;
    AppBarLayout mAppBarLayoutBar;
    RatingBar movieRatingBar;
    TextView movieTitleTextView;
    TextView movieOriginalTitleTextView;
    TextView movieRatingTextView;
    TextView movieReleaseDateTextView;
    TextView moviePopularityTextView;
    TextView movieOverViewTextView;
    TextView movieVoteCountTextView;
    TextView movieRestrictionRatingTextView;

    ParcelableBitmap mBackDropBitmap;
    ParcelableBitmap mPosterBitmap;

    private String mPosterURL;
    private String mBackDropURL;
    private boolean mIsStateRestored = false;
    private final static String MOVIE_PARCELABLE_SAVED_KEY = "movieParcelableDetailViewFragmentKey";

    public static void setSharableYoutubeTrailerLink(String mSharableYoutubeTrailerLink) {
        DisplayExpandedMovieDataFragment.mSharableYoutubeTrailerLink = mSharableYoutubeTrailerLink;
    }

    public static String getSharableYoutubeTrailerLink() {
        return mSharableYoutubeTrailerLink;
    }

    private static String mSharableYoutubeTrailerLink = null;
    RecyclerView mReviewsRecyclerView;
    RecyclerView mTrailersRecyclerView;
    CardView mFavouritesCard;
    RatingBar favouritesRatingBar;
    ReviewRecyclerViewCursorAdapter reviewsRecyclerViewAdapter = new ReviewRecyclerViewCursorAdapter(getActivity(), null,mMovieData);
    TrailersRecyclerViewCursorAdapter trailersRecyclerViewAdapter = new TrailersRecyclerViewCursorAdapter(getActivity(), null);
    private final int TRAILERS_LOADER_ID = 12760;
    private final int REVIEWS_LOADER_ID = 12761;
    private final int FAVOURITES_LOADER_ID = 12762;
    private final int IS_FAVOURED_LOADER_ID = 12763;
    private boolean isMovieFavoured = false;
    private boolean formatedMovieDataString;

    public DisplayExpandedMovieDataFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initializeLoaders();
        super.onActivityCreated(savedInstanceState);
    }

    private void initializeLoaders() {
        getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMovieData = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_display_expanded_movie_data, container, false);
        setHasOptionsMenu(true);
        initializeViewElements(view);
        if (mMovieData != null) {
            updateUIWithMovieData();
            //fetchMovieTrailersReviews();
        }
        return view;
    }

    public void fetchMovieTrailersReviews() {
        if (null != mMovieData)
            new DownloadData(new AsyncResult() {
                @Override
                public void onResultJSON(JSONObject object) throws JSONException {

                }

                @Override
                public void onResultString(String stringObject) {
                    if (getActivity() != null) {
                        if (MoviePosterGridActivity.isInTwoPaneMode()) {
                            ((MoviePosterGridActivity) getActivity())
                                    .showNetworkConnectivityDialogue("Please Connect to a working Network");
                        } else {
                            ((DisplayDetailedMovieData) getActivity())
                                    .showNetworkConnectivityDialogue("Please Connect to a working Network");
                        }
                    }
                }

                @Override
                public void onResultParsedIntoMovieList(ArrayList<MovieDataInstance> movieList) {

                }

                @Override
                public void onTrailersResultParsed(ArrayList<MovieTrailerInstance> trailerList) {
                    if (getActivity() != null) {
                        getLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, DisplayExpandedMovieDataFragment.this);
                    }
                }

                @Override
                public void onReviewsResultParsed(ArrayList<MovieReviewInstance> reviewList) {
                    if (getActivity() != null) {
                        getLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, DisplayExpandedMovieDataFragment.this);
                    }
                }
            }
                    , getActivity()
            ).execute(
                    APIUrls.buildMovieReviewsURL(mMovieData.getMovieID() + "").toString()
                    , APIUrls.buildMovieTrailerURL(mMovieData.getMovieID() + "").toString()
            );
    }

    private void updateUIWithMovieData() {

        movieRatingTextView.setText(String.format(getString(R.string.string_format_average_voting)
                , mMovieData.getAverageVoting()));

        moviePopularityTextView.setText(String.format(getString(R.string.string_format_popularity)
                , mMovieData.getPopularity()));
        movieTitleTextView.setText(mMovieData.getTitle());
        if (!mMovieData.getTitle().trim().equalsIgnoreCase(mMovieData.getOriginalTitle().trim())) {
            movieOriginalTitleTextView.setVisibility(View.VISIBLE);
            movieOriginalTitleTextView.setText(mMovieData.getOriginalTitle());
        } else {
            movieOriginalTitleTextView.setVisibility(View.GONE);
        }
        movieReleaseDateTextView.setText(mMovieData.getReleaseDate());
        movieOverViewTextView.setText(mMovieData.getOverView());
        movieOriginalTitleTextView.setMaxLines(1);
        movieVoteCountTextView.setText(String.format("%d", mMovieData.getVoteCount()));
        movieRatingBar.setIsIndicator(true);
        movieRatingBar.setRating((float) (mMovieData.getAverageVoting() / 2.0));
        mPosterURL = APIUrls.BASE_IMAGE_URL
                + "/"
                + APIUrls.API_IMG_W_500
                + mMovieData.getPOSTER_PATH();
        mBackDropURL = APIUrls.BASE_IMAGE_URL
                + "/"
                + APIUrls.API_IMG_W_500
                + mMovieData.getBackDropPath();
        Log.e("BackDropURL",APIUrls.BASE_IMAGE_URL
                + "/"
                + APIUrls.API_IMG_W_780
                + mMovieData.getBackDropPath());
        mSharableYoutubeTrailerLink = null;
        setUpAndInitializeRecyclerView(mReviewsRecyclerView
                , new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
                , reviewsRecyclerViewAdapter);
        setUpAndInitializeRecyclerView(mTrailersRecyclerView
                , new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
                , trailersRecyclerViewAdapter);
        if (!isMovieFavoured) {
            loadImageWithPicasso(mPosterURL, mPosterImageView, "poster");
            loadImageWithPicasso(mBackDropURL, mBackDropImageView, "backDrop");
        }
        //Reset Booleans So that user preferences are restored from the SQLite Database
        isMovieFavoured = false;
        //Now if the movie is previously favoured by the user then mMovieFavoured will be set to true.
        if (mMovieData != null /*&& mIsStateRestored*/) {
            /*Log.e("DetailCursorDebug", "Initializing Loaders");
            getLoaderManager().restartLoader(REVIEWS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(TRAILERS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(IS_FAVOURED_LOADER_ID, null, this);
            getLoaderManager().restartLoader(FAVOURITES_LOADER_ID, null, this);
            mIsStateRestored = false;
        } else {*/
            destroyAllLoaders();
            getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
            getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
            getLoaderManager().initLoader(IS_FAVOURED_LOADER_ID, null, this);
            getLoaderManager().initLoader(FAVOURITES_LOADER_ID, null, this);
        }
    }

    private void destroyAllLoaders() {
        getLoaderManager().destroyLoader(REVIEWS_LOADER_ID);
        getLoaderManager().destroyLoader(TRAILERS_LOADER_ID);
        getLoaderManager().destroyLoader(IS_FAVOURED_LOADER_ID);
        getLoaderManager().destroyLoader(FAVOURITES_LOADER_ID);
    }

    private void initializeViewElements(View view) {

        mPosterImageView = (ImageView) view.findViewById(R.id.moviePosterImageView);

        mFavouritesCard = (CardView) view.findViewById(R.id.favouritesCard);
        favouritesRatingBar = (RatingBar) view.findViewById(R.id.favouritesIndicator);
        favouritesRatingBar.setIsIndicator(true);
        favouritesRatingBar.setNumStars(1);

        mBackDropImageView = (ImageView) view.findViewById(R.id.backDropImageView);
        mCollapsingToolBar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolBar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mAppBarLayoutBar = (AppBarLayout) view.findViewById(R.id.app_bar);
        mContentNestedScrollView = (NestedScrollView) view.findViewById(R.id.contentNestedScrololView);

        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.reviewRecyclerView);
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.trailersRecyclerView);

        posterCardView = (CardView) view.findViewById(R.id.posterCardView);
        posterCardView.setOnClickListener(this);
        movieRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        movieTitleTextView = (TextView) view.findViewById(R.id.movieTitleTextView);
        movieOriginalTitleTextView = (TextView) view.findViewById(R.id.originalTitle);
        movieRatingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        moviePopularityTextView = (TextView) view.findViewById(R.id.popularityTextView);
        movieReleaseDateTextView = (TextView) view.findViewById(R.id.releaseDateTextView);
        movieOverViewTextView = (TextView) view.findViewById(R.id.overViewTextView);
        movieVoteCountTextView = (TextView) view.findViewById(R.id.voteCountTextView);
        movieRestrictionRatingTextView = (TextView) view.findViewById(R.id.adultForChildrenTextView);

        mContentNestedScrollView.setOnScrollChangeListener(this);
        mShareCard = (CardView) view.findViewById(R.id.shareCard);
        mShareCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieData != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, getFormattedMovieDataString());
                    intent.setType("text/plain");
                    startActivity(intent);
                }
            }
        });
        mFavouritesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMovieFavoured) {
                    getActivity().getContentResolver()
                            .delete(MovieContract
                                    .UserFavourite
                                    .buildFavouritesUriWithMovieId(
                                            String.valueOf(mMovieData.getMovieID())
                                    ), null, null);
                    isMovieFavoured = false;
                    /*TODO:Update UI To show Un-Favoured Movie*/
                    mFavouritesCard.setCardBackgroundColor(getResources().getColor(R.color.md_black_1000));
                    Toast.makeText(getActivity(), "Unfavourited", Toast.LENGTH_LONG).show();
                } else {
                    ContentValues temp = new ContentValues();
                    temp.put(MovieContract.UserFavourite.COLUMN_MOVIE_ID, mMovieData.getMovieID());
                    getActivity().getContentResolver()
                            .insert(MovieContract.UserFavourite.FAVOURITES_CONTENT_URI
                                    , temp);
                    if (mPosterBitmap != null) {
                        runAsyncMediaStorage();
                    }

                    isMovieFavoured = true;
                    /*TODO:Update UI To show Favoured Movie*/
                    mFavouritesCard.setCardBackgroundColor(getResources().getColor(R.color.ratingCardBackgroundColor));
                    Toast.makeText(getActivity(), "Favourited", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                        , AsyncMediaStorageClass.TYPE_POSTER
                )
                , AsyncMediaStorageClass.SET_IMAGE_VIEW_BITMAP_TASK
                , null
                , posterImageView
        ).setOnBitmapRenderedListener(new AsyncMediaStorageClass.OnBitmapRenderedListener() {
            @Override
            public void onBitmapRendered(String errorMessage, Bitmap bitmap) {
                mPosterImageView.setImageBitmap(bitmap);
            }
        }).execute();
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

    private void setUpAndInitializeRecyclerView(
            RecyclerView recyclerView
            , LinearLayoutManager recyclerViewLayoutManager
            , CursorRecyclerViewAdapter cursorRecyclerViewAdapter) {
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(cursorRecyclerViewAdapter);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.posterCardView:
                startActivity(
                        new Intent(getActivity(), MoviePosterDisplayActivity.class)
                                .putExtra(
                                        "mPosterURL"
                                        , APIUrls.BASE_IMAGE_URL
                                                + "/"
                                                + APIUrls.API_IMG_W_780
                                                + mMovieData.getPOSTER_PATH()
                                )
                );
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.moviePosterImageView:
                loadImageWithPicasso(mPosterURL, mPosterImageView, "poster");
                break;
            case R.id.backDropImageView:
                loadImageWithPicasso(mBackDropURL, mBackDropImageView, "backDrop");
                break;
        }
        return true;
    }

    private void loadImageWithPicasso(final String URL, final ImageView mImageView, final String imageType) {
        Target imageBitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBackDropBitmap = new ParcelableBitmap(bitmap);
                mImageView.setImageBitmap(bitmap);
                if (imageType.equals("poster")) {
                    mPosterBitmap = new ParcelableBitmap(bitmap);
                } else {
                    mBackDropBitmap = new ParcelableBitmap(bitmap);
                }
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                        int primary = getResources().getColor(R.color.colorPrimary);
                        mCollapsingToolBar.setContentScrimColor(palette.getMutedColor(primary));
                        mCollapsingToolBar.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                        mShareCard.setCardBackgroundColor(palette.getDarkVibrantColor(primaryDark));
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                loadImageWithPicasso(URL, mImageView, imageType);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if (mImageView.getTag() == null) {
            mImageView.setTag(imageBitmapTarget);
        }
        Picasso.with(getActivity()).load(URL).into(imageBitmapTarget);
    }

    @Override
    public void onMovieSelectDispatched(MovieDataInstance instance) {
        mMovieData = instance;
        updateUIWithMovieData();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mMovieData != null) {
            switch (id) {
                case REVIEWS_LOADER_ID:
                    Log.e("DetailCursorDebug", "Reviews : " + id);
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.MovieReviews.buildMovieReviewUriWithMovieId(String.valueOf(mMovieData.getMovieID()))
                            , null
                            , null
                            , null
                            , null
                    );
                case TRAILERS_LOADER_ID:
                    Log.e("DetailCursorDebug", "Trailers : " + id);
                    return new CursorLoader(
                            getActivity()
                            , MovieContract.MovieTrailers.buildMovieTrailerUriWithMovieId(String.valueOf(mMovieData.getMovieID()))
                            , null
                            , null
                            , null
                            , null
                    );
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case REVIEWS_LOADER_ID:
                Log.e("DetailCursorDebug", "Size : " + data.getCount());
                if (data.getCount() == 0) {
                    fetchMovieTrailersReviews();
                } else {
                    reviewsRecyclerViewAdapter.swapCursor(data);
                    reviewsRecyclerViewAdapter.notifyDataSetChanged();
                }

                break;
            case TRAILERS_LOADER_ID:
                Log.e("DetailCursorDebug", "Size : " + data.getCount());
                if (data.getCount() == 0) {
                    fetchMovieTrailersReviews();
                } else {
                    Cursor temp = data;
                    temp.moveToFirst();
                    mSharableYoutubeTrailerLink = APIUrls.buildYoutubeTrailerURI(
                            (MovieTrailerInstance.getMovieTrailerInstanceFromCursor(temp)).getTRAILER_KEY()
                    ).toString();
                    Log.e("TrailerDebug", mSharableYoutubeTrailerLink);
                    trailersRecyclerViewAdapter.swapCursor(data);
                    trailersRecyclerViewAdapter.notifyDataSetChanged();
                }
                break;
            case IS_FAVOURED_LOADER_ID:
                Log.e("DatabaseDebug", "Load Finished");
                //Log.e("DetailCursorDebug", "Size : " + data.getCount());
                if (data.getCount() == 0) {
                    isMovieFavoured = false;
                    Log.e("DatabaseDebug", "Movie not found as favoured");
                    Toast.makeText(getActivity(), "Movie NOT Favoured", Toast.LENGTH_SHORT).show();
                    /*TODO:Update UI To Show That Movie is NOT Favoured*/
                    mFavouritesCard.setCardBackgroundColor(getResources().getColor(R.color.md_black_1000));

                } else {
                    Log.e("DatabaseDebug", "Movie found as favoured");
                    isMovieFavoured = true;
                    Toast.makeText(getActivity(), "Movie Favoured", Toast.LENGTH_SHORT).show();
                    /*TODO:Update UI To Show That Movie is Favoured*/
                    mFavouritesCard.setCardBackgroundColor(getResources().getColor(R.color.ratingCardBackgroundColor));
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
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewsRecyclerViewAdapter.swapCursor(null);
        trailersRecyclerViewAdapter.swapCursor(null);
    }

    @Override
    public void onMoreReviewDataRequested(int currentPage) {

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
}
