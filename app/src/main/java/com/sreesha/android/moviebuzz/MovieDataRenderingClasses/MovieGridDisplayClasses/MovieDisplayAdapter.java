package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

/**
 * Created by Sreesha on 28-01-2016.
 */
public class MovieDisplayAdapter extends RecyclerView.Adapter<MovieDisplayAdapter.ViewHolder> {
    String URL;
    Context mContext;
    private final int minimumOffset = 10;
    MoviePosterGridFragment.NotifyMovieClick mMovieDisplaySelectionListener;
    ArrayList<MovieDataInstance> movieList;

    MovieDisplayAdapter() {
    }

    MovieDisplayAdapter(ArrayList<MovieDataInstance> movieList, Context mContext) {
        this.mContext = mContext;
        mMovieDisplaySelectionListener = (MoviePosterGridActivity) mContext;
        this.movieList = movieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_movie_poster_display, parent, false);
        mContext = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("MovieTypes", "onBindViewHolder");
        holder.posterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MovieDataInstance> parcelableIntentList = new ArrayList<MovieDataInstance>();
                parcelableIntentList.add(
                        movieList.get(holder.getAdapterPosition())
                );
                mMovieDisplaySelectionListener.onMovieClicked(
                        parcelableIntentList
                        , movieList.get(holder.getAdapterPosition())
                );
            }
        });

        holder.movieOriginalTitleTextView.setText(movieList.get(position).getOriginalTitle());
        /*Disable User Interaction with the rating bar*/
        holder.movieRatingBar.setIsIndicator(true);
        /*Convert the Average voting from a scale of 0 to 10 to a scale of 0 to 5*/
        holder.movieRatingBar.setRating((float) (movieList.get(position).getAverageVoting() / 2.0));
        /*Remove All Spaces in the Date And DisplayDate*/
        holder.movieReleaseDateTextView.setText(movieList.get(position).getReleaseDate().replace(" ", ""));

        URL = APIUrls.BASE_IMAGE_URL
                + "/" + APIUrls.API_IMG_W_342
                + movieList.get(position)
                .getPOSTER_PATH();

        Picasso
                .with(mContext)
                .load(URL)
                .into(holder.moviePosterImageView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        CustomAnimator.slideAnimRecyclerView(holder,true);

        CustomAnimator.createCircularRevealEffect(
                holder.movieDetailCard
                , holder.movieDetailCard.getHeight()
                , holder.movieDetailCard.getWidth()
                , 20
                , 1500
        );
    }

    @Override
    public int getItemCount() {
        if (movieList != null)
            return movieList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView moviePosterImageView;
        public CardView posterCardView;
        public CardView movieDetailCard;
        RatingBar movieRatingBar;
        TextView movieReleaseDateTextView;
        TextView movieOriginalTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterCardView = (CardView) itemView.findViewById(R.id.posterCardView);
            moviePosterImageView = (ImageView) itemView.findViewById(R.id.moviePosterImageView);
            movieDetailCard = (CardView) itemView.findViewById(R.id.movieDetailCard);
            movieRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBarIndicator);
            movieReleaseDateTextView = (TextView) itemView.findViewById(R.id.releaseDateTextView);
            movieOriginalTitleTextView = (TextView) itemView.findViewById(R.id.originalTitleTextView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setMovieList(ArrayList<MovieDataInstance> movieList) {
        this.movieList = movieList;
    }
}
