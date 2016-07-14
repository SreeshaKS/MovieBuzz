package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.DataHandlerClasses.CursorRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView.AsyncMediaStorageClass;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sreesha on 14-02-2016.
 */
public class MovieRecyclerViewCursorAdapter
        extends CursorRecyclerViewAdapter<MovieRecyclerViewCursorAdapter.ViewHolder> {

    String URL;
    Context mContext;
    OnMoreDataRequestedListener mOnMoreDataRequestedListener;
    MoviePosterGridFragment.NotifyMovieClick mMovieSelectionListener = null;
    private boolean isFreshInstantiation;

    public MovieRecyclerViewCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        try {
            mMovieSelectionListener = (MoviePosterGridFragment.NotifyMovieClick) mContext;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        isFreshInstantiation = true;
        Log.e("DetailCursorDebug", "InitReviewsAdapter");
    }

    public MovieRecyclerViewCursorAdapter(Context context, Cursor cursor, boolean similarMovies) {
        super(context, cursor);
        mContext = context;
        try {
            if (similarMovies)
                mMovieSelectionListener = null;
            else
                mMovieSelectionListener = (MoviePosterGridFragment.NotifyMovieClick) mContext;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        isFreshInstantiation = true;
        Log.e("DetailCursorDebug", "InitReviewsAdapter");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_movie_poster_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final Cursor cursor) {
        Log.e("Debug", "onBindViewHolderCalled");
        final MovieDataInstance instance = MovieDataInstance.getMovieDataInstanceFromCursor(cursor);

        if (isFreshInstantiation && MoviePosterGridActivity.isInTwoPaneMode()) {
            isFreshInstantiation = false;
            ArrayList<MovieDataInstance> parcelableIntentList = new ArrayList<MovieDataInstance>();
            parcelableIntentList.add(instance);
            if (mMovieSelectionListener != null)
                mMovieSelectionListener.onMovieClicked(parcelableIntentList, instance);
        }
        Log.e("LoaderOnBindViewCB", "Outside IF Cursor Item Count:\t" + cursor.getCount()
                + "\nCursor Item Position" + cursor.getPosition());
        loadUIWithCursorData(
                holder
                , instance
        );
        Log.e("GenreDebug", instance.getGENRE_ID_JSON_ARRAY_STRING());
        holder.posterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MovieDataInstance> parcelableIntentList = new ArrayList<MovieDataInstance>();
                parcelableIntentList.add(instance);
                if (mMovieSelectionListener != null)
                    mMovieSelectionListener.onMovieClicked(parcelableIntentList, instance);
            }
        });
        if (!PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(mContext.getString(R.string.poster_storage_checkbox_preference_key), true)) {
            String posterPath = AsyncMediaStorageClass.getPosterFileAbsolutePath(
                    AsyncMediaStorageClass.getFormattedPosterFileName(
                            instance.getMovieID()
                            , AsyncMediaStorageClass.TYPE_POSTER)
            );
            String backDropPath = AsyncMediaStorageClass.getPosterFileAbsolutePath(
                    AsyncMediaStorageClass.getFormattedPosterFileName(
                            instance.getMovieID()
                            , AsyncMediaStorageClass.TYPE_POSTER)
            );
            //Store Images
        }
    }

    private void loadUIWithCursorData(final ViewHolder holder, final MovieDataInstance instance) {

        /*Disable User Interaction with the rating bar*/
        holder.movieRatingBar.setIsIndicator(true);
        holder.movieOriginalTitleTextView.setText(instance.getOriginalTitle());
        /*Disable User Interaction with the rating bar*/
        holder.movieRatingBar.setIsIndicator(true);
        /*Convert the Average voting from a scale of 0 to 10 to a scale of 0 to 5*/
        holder.movieRatingBar.setRating((float) (instance.getAverageVoting() / 2.0));
        /*Remove All Spaces in the Date And DisplayDate*/
        holder.movieReleaseDateTextView.setText(instance.getReleaseDate().replace(" ", ""));

        URL = APIUrls.BASE_IMAGE_URL
                + "/" + APIUrls.API_IMG_W_342
                + instance
                .getPOSTER_PATH();
        if (PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(
                        mContext.getString(R.string.favourites_checkbox_preference_key)
                        , false)
                ) {
            if (holder.moviePosterImageView.getTag() != null) {
                try {
                    ((AsyncMediaStorageClass) holder.moviePosterImageView.getTag()).cancel(true);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
            AsyncMediaStorageClass storageAsyncTask = new AsyncMediaStorageClass(
                    AsyncMediaStorageClass.getFormattedPosterFileName(
                            instance.getMovieID()
                            , AsyncMediaStorageClass.TYPE_POSTER
                    )
                    , AsyncMediaStorageClass.SET_IMAGE_VIEW_BITMAP_TASK
                    , null
                    , holder.moviePosterImageView
            );
            storageAsyncTask.setOnBitmapRenderedListener(new AsyncMediaStorageClass.OnBitmapRenderedListener() {
                @Override
                public void onBitmapRendered(String errorMessage, Bitmap bitmap) {
                    if (bitmap != null && errorMessage.equals(AsyncMediaStorageClass.RESULT_SUCCESS)) {
                        Log.e("PicassoDebug", "Loading Favourite image poster Using Picasso Error message : " + errorMessage);
                        holder.moviePosterImageView.setImageBitmap(bitmap);
                    } else {
                        Log.e("PicassoDebug", "Loading Favourite Image poster Using Picasso Error message : " + errorMessage);
                        loadImagesUsingPicasso(holder);
                    }
                }
            }).execute();
            holder.moviePosterImageView.setTag(storageAsyncTask);
        } else {
            Log.e("PicassoDebug", "Loading Image poster Using Picasso ");
            loadImagesUsingPicasso(holder);
        }
    }

    public void loadImagesUsingPicasso(final ViewHolder holder) {
        Target posterImageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.moviePosterImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        if (holder.moviePosterImageView.getTag() != null) {
            holder.moviePosterImageView.setTag(posterImageTarget);
        }
        Picasso
                .with(mContext)
                .load(URL)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.ic_movie_white_48dp)
                .error(R.drawable.ic_error_white_48dp)
                .into(posterImageTarget);
    }

    public void storeImage(MovieDataInstance instance, String posterType) {
        if (!new File(
                AsyncMediaStorageClass.getPosterFileAbsolutePath(
                        AsyncMediaStorageClass.getFormattedPosterFileName(
                                instance.getMovieID(), posterType
                        )
                )
        ).exists()) {
            new AsyncMediaStorageClass(
                    AsyncMediaStorageClass.getFormattedPosterFileName(
                            instance.getMovieID()
                            , posterType
                    )
                    , AsyncMediaStorageClass.BITMAP_STORAGE_TASK
                    , null
                    , null
            ).setOnImageStoredListener(new AsyncMediaStorageClass.OnImageStoredListener() {
                @Override
                public void onImageStored(String errorMessage, String imagePath) {

                }
            }).execute();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView moviePosterImageView;
        public CardView posterCardView;
        public CardView movieDetailCard;
        RatingBar movieRatingBar;
        TextView movieReleaseDateTextView;
        TextView movieOriginalTitleTextView;

        public ViewHolder(View view) {
            super(view);
            posterCardView = (CardView) itemView.findViewById(R.id.posterCardView);
            moviePosterImageView = (ImageView) itemView.findViewById(R.id.moviePosterImageView);
            movieDetailCard = (CardView) itemView.findViewById(R.id.movieDetailCard);
            movieRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBarIndicator);
            movieReleaseDateTextView = (TextView) itemView.findViewById(R.id.releaseDateTextView);
            movieOriginalTitleTextView = (TextView) itemView.findViewById(R.id.originalTitleTextView);
        }
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        CustomAnimator.slideAnimRecyclerView(holder, true);
        CustomAnimator.createCircularRevealEffect(
                holder.movieDetailCard
                , holder.movieDetailCard.getHeight()
                , holder.movieDetailCard.getWidth()
                , 20
                , 1500
        );
        super.onViewAttachedToWindow(holder);
    }

    public void setOnMoreDataRequestedListener(OnMoreDataRequestedListener listener) {
        mOnMoreDataRequestedListener = listener;
    }

}
