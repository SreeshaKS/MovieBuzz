package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.AsyncMediaStorageClass;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.PreferenceKeys;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MovieDetailDisplayFragment extends Fragment
        implements MovieTabsDetailFragment.MovieDetailedDataFragmentInterface
        , View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    MovieDataInstance mMovieData = null;
    ImageView mPosterImageView;
    CardView posterCardView;
    RatingBar movieRatingBar;
    TextView movieTitleTextView;
    TextView movieOriginalTitleTextView;
    TextView movieRatingTextView;
    TextView movieReleaseDateTextView;
    TextView moviePopularityTextView;
    TextView movieOverViewTextView;
    TextView movieVoteCountTextView;

    ParcelableBitmap mPosterBitmap;

    private String mPosterURL;
    private String mBackDropURL;
    private boolean mIsStateRestored = false;
    private final static String MOVIE_PARCELABLE_SAVED_KEY = "movieParcelableDetailViewFragmentKey";

    RecyclerView mCrewRecyclerView;
    private static final int CREW_LOADER_ID = 1;
    RecyclerView genreRecyclerView;
    GenreRecyclerViewAdapter mGenreRecyclerViewAdapter;

    public MovieDetailDisplayFragment() {
        // Required empty public constructor
    }

    private class GenreRecyclerViewAdapter extends RecyclerView.Adapter<GenreRecyclerViewAdapter.ViewHolder> {
        MovieDataInstance mMovieData;
        JSONArray genreArray;
        JSONArray movieGenreArray;

        ArrayList<String> genreDataSet;

        public GenreRecyclerViewAdapter(MovieDataInstance mMovieData) {
            this.mMovieData = mMovieData;
            genreDataSet = new ArrayList<>();

            try {
                genreArray = new JSONObject(PreferenceKeys.GENRE_JSON_OBJECT).getJSONArray("genres");
                movieGenreArray = new JSONArray(mMovieData.getGENRE_ID_JSON_ARRAY_STRING());
                for (int i = 0; i < movieGenreArray.length(); i++) {
                    for (int j = 0; j < genreArray.length(); j++) {
                        if (movieGenreArray.getInt(i) == genreArray.getJSONObject(j).getInt("id")) {
                            genreDataSet.add(genreArray.getJSONObject(j).getString("name"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.genre_recycler_view_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.genreTextView.setText(genreDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return genreDataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView genreTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                genreTextView = (TextView) itemView.findViewById(R.id.genreTextView);
            }
        }
    }

    public static MovieDetailDisplayFragment newInstance(String param1, String param2) {
        MovieDetailDisplayFragment fragment = new MovieDetailDisplayFragment();
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
        if (savedInstanceState != null) {
            mMovieData = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED_KEY);
            mIsStateRestored = true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED_KEY, mMovieData);
    }

    public void setMovieData(MovieDataInstance mMovieData) {
        this.mMovieData = mMovieData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        initializeViewElements(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMovieData != null) {
            if (mIsStateRestored) {
                updateUIWithMovieData();
            } else {
                (
                        (MovieTabsDetailFragment)
                                (getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT))
                ).OnUIReadyToBeUpdated(UIUpdatableInterface.MOVIE_DETAIL_FRAGMENT);
            }
        }
    }

    private void updateUIWithMovieData() {
        if (genreRecyclerView != null) {
            mGenreRecyclerViewAdapter = new GenreRecyclerViewAdapter(mMovieData);
            LinearLayoutManager mLinearLayoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            genreRecyclerView.setLayoutManager(mLinearLayoutManager);
            genreRecyclerView.setAdapter(mGenreRecyclerViewAdapter);
        }
        movieRatingTextView.setText(String.format(getString(R.string.string_format_average_voting)
                , mMovieData.getAverageVoting()));

        moviePopularityTextView.setText(String.format(getString(R.string.string_format_popularity)
                , mMovieData.getPopularity()));

        if (!mMovieData.getTitle().trim().equalsIgnoreCase(mMovieData.getOriginalTitle().trim())) {
            movieOriginalTitleTextView.setVisibility(View.VISIBLE);
            movieOriginalTitleTextView.setText(mMovieData.getOriginalTitle());
            movieTitleTextView.setVisibility(View.VISIBLE);
            movieTitleTextView.setText(mMovieData.getTitle());
        } else {
            movieOriginalTitleTextView.setVisibility(View.VISIBLE);
            movieOriginalTitleTextView.setText(mMovieData.getOriginalTitle());
            movieTitleTextView.setVisibility(View.GONE);
        }
        movieReleaseDateTextView.setText(mMovieData.getReleaseDate());
        movieOverViewTextView.setText(mMovieData.getOverView());
        movieOriginalTitleTextView.setMaxLines(1);
        movieRatingBar.setIsIndicator(true);
        movieRatingBar.setRating((float) (mMovieData.getAverageVoting() / 2.0));
        //movieVoteCountTextView.setText(String.format("%d", mMovieData.getVoteCount()));
        if (((MovieTabsDetailFragment)
                getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag(UIUpdatableInterface.MOVIE_DETAIL_TABS_FRAGMENT)
        ).isMovieFavoured()) {
            runAsyncMediaRetrieval(mPosterImageView);
        } else {
            mPosterURL = APIUrls.BASE_IMAGE_URL
                    + "/"
                    + APIUrls.API_IMG_W_500
                    + mMovieData.getPOSTER_PATH();
            loadImageWithPicasso(mPosterURL, mPosterImageView, "poster");
        }
    }

    private void initializeViewElements(View view) {

        genreRecyclerView = (RecyclerView) view.findViewById(R.id.genreRecyclerView);

        movieRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        mPosterImageView = (ImageView) view.findViewById(R.id.moviePosterImageView);

        posterCardView = (CardView) view.findViewById(R.id.movieDetailPosterCardView);
        posterCardView.setOnClickListener(this);

        movieTitleTextView = (TextView) view.findViewById(R.id.movieTitleTextView);
        try {
            movieOriginalTitleTextView = (TextView) view.findViewById(R.id.originalTitleTextView);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        movieRatingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        moviePopularityTextView = (TextView) view.findViewById(R.id.popularityTextView);
        movieReleaseDateTextView = (TextView) view.findViewById(R.id.releaseDateTextView);
        movieOverViewTextView = (TextView) view.findViewById(R.id.overViewTextView);
        //movieVoteCountTextView = (TextView) view.findViewById(R.id.voteCountTextView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnMovieDataChanged(MovieDataInstance movieInstance) {
        try {
            mMovieData = movieInstance;
            updateUIWithMovieData();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void loadImageWithPicasso(final String URL, final ImageView mImageView, final String imageType) {
        Target imageBitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mImageView.setImageBitmap(bitmap);
                if (imageType.equals("poster")) {
                    mPosterBitmap = new ParcelableBitmap(bitmap);
                } else {

                }
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette)throws IllegalStateException {
                        if (getActivity() != null) {
                            int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                            int primary = getResources().getColor(R.color.colorPrimary);
                        }
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

    private void runAsyncMediaRetrieval(ImageView posterImageView) {
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
                if (errorMessage.equals(AsyncMediaStorageClass.RESULT_SUCCESS))
                    mPosterImageView.setImageBitmap(bitmap);
            }
        }).execute();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
