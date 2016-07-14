package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.DetailView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.DataHandlerClasses.CursorRecyclerViewAdapter;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieTrailerInstance;
import com.sreesha.android.moviebuzz.R;

/**
 * Created by Sreesha on 06-03-2016.
 */
public class TrailersRecyclerViewCursorAdapter extends CursorRecyclerViewAdapter<TrailersRecyclerViewCursorAdapter.ViewHolder> {
    Context mContext;

    public TrailersRecyclerViewCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        Log.e("DetailCursorDebug", "InitTrailersAdapter");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        Log.e("DetailCursorDebug", "Trailer Item Position" + cursor.getPosition());
        final MovieTrailerInstance instance = MovieTrailerInstance.getMovieTrailerInstanceFromCursor(cursor);
        if (cursor.getPosition() == 1) {
            DisplayExpandedMovieDataFragment.setSharableYoutubeTrailerLink(
                    APIUrls
                            .buildYoutubeTrailerURI(instance.getTRAILER_KEY())
                            .build().toString()
            );
            Log.e("TrailerDebug", APIUrls
                    .buildYoutubeTrailerURI(instance.getTRAILER_KEY())
                    .build().toString());
        }
        Picasso
                .with(mContext)
                .load(
                        APIUrls
                                .buildYoutubeThumbNailURL(instance.getTRAILER_KEY(), "hq")
                                .toString()
                )
                .placeholder(R.drawable.ic_movie_white_48dp)
                .error(R.drawable.ic_error_white_48dp)
                .into(holder.trailerThumbnailImageView)
        ;
        holder.trailerNameTextView.setText(instance.getTRAILER_NAME().trim());
        holder.trailerThumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW
                        ,
                        Uri.parse(
                                APIUrls
                                        .buildYoutubeTrailerURI(instance.getTRAILER_KEY())
                                        .build().toString()
                        )
                );
                v.getContext().startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView trailerThumbnailImageView;
        public TextView trailerNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerThumbnailImageView = (ImageView) itemView.findViewById(R.id.trailerThumbNailImageView);
            trailerNameTextView = (TextView) itemView.findViewById(R.id.trailerNameTextView);
        }
    }
    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        CustomAnimator.slideAnimRecyclerView(holder,true);
    }
}
