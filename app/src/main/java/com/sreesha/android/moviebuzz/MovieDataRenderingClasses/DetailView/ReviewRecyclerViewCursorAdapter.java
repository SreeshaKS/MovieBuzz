package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.DataHandlerClasses.CursorRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.R;

/**
 * Created by Sreesha on 06-03-2016.
 */
public class ReviewRecyclerViewCursorAdapter extends CursorRecyclerViewAdapter<ReviewRecyclerViewCursorAdapter.ViewHolder> {
    Context mContext;
    OnMoreReviewDataRequestedListener reviewDataRequestedListener;
    String mCurrentMovieName = "defaultMovieName";
    MovieDataInstance mMovieDataInstance;

    public void setMovieDataInstance(MovieDataInstance mMovieDataInstance) {
        this.mMovieDataInstance = mMovieDataInstance;
    }


    public ReviewRecyclerViewCursorAdapter(Context context, Cursor cursor, MovieDataInstance movieDataInstance) {
        super(context, cursor);
        mContext = context;
        this.mMovieDataInstance = movieDataInstance;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_single_recycler_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        Log.e("TabsDebug", "onBindViewHolder called CursorCount : " + cursor.getCount());
        final MovieReviewInstance reviewsInstance = MovieReviewInstance.getMovieReviewInstanceFromCursor(cursor);
        holder.contentOverViewTextView.setText(reviewsInstance.getREVIEW_CONTENT());
        holder.authorTextView.setText(reviewsInstance.getAUTHOR());
        holder.reviewLinkTextView.setText(reviewsInstance.getREVIEW_URL());

        if (mMovieDataInstance != null)
            holder.reviewMovieNameTextView.setText(mMovieDataInstance.getTitle());

        holder.reviewShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getFormattedReviewDataString(reviewsInstance));
                intent.setType("text/plain");
                v.getContext().startActivity(intent);
            }
        });
    }

    String getFormattedReviewDataString(MovieReviewInstance reviewsInstance) {
        String title = "";
        if (mMovieDataInstance != null)
            title = mMovieDataInstance.getTitle();

        return ("Author : " +
                reviewsInstance.getAUTHOR() + "\n"
                + "Movie : " + title
                + "\n\n" + reviewsInstance.getREVIEW_CONTENT()
                + "\n"
                + "Web : " + reviewsInstance.getREVIEW_URL());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView expandedReviewContentCardView;
        public TextView authorTextView;
        public TextView contentOverViewTextView;
        public TextView reviewMovieNameTextView;
        public ImageView reviewShareImageView;
        public TextView reviewLinkTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            expandedReviewContentCardView = (CardView) itemView.findViewById(R.id.reviewExpandedContentCard);
            authorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
            contentOverViewTextView = (TextView) itemView.findViewById(R.id.reviewExpandedContentTextView);
            reviewMovieNameTextView = (TextView) itemView.findViewById(R.id.reviewMovieNameView);
            reviewShareImageView = (ImageView) itemView.findViewById(R.id.reviewShareImageView);
            reviewLinkTextView = (TextView) itemView.findViewById(R.id.reviewLinkTextView);
        }
    }

    public void setCurrentMovieName(String movieName) {
        mCurrentMovieName = movieName;
    }

    public String getCurrentMovieName() {
        return mCurrentMovieName;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        CustomAnimator.slideAnimRecyclerView(holder, true);
    }
}
