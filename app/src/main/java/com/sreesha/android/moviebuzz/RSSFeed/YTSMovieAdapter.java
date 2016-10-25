package com.sreesha.android.moviebuzz.RSSFeed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Sreesha on 19-10-2016.
 */

public class YTSMovieAdapter extends RecyclerView.Adapter<YTSMovieAdapter.ViewHolder> {
    ArrayList<YTSMovie> mYTSMovieArrayList;

    YTSMovieAdapter(ArrayList<YTSMovie> mYTSMovieArrayList) {
        this.mYTSMovieArrayList = mYTSMovieArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.torrent_movie_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        YTSMovie mMovie = mYTSMovieArrayList.get(position);
        Picasso.with(holder.itemView.getContext())
                .load(mMovie.getBackground_image_original())
                .into(holder.mBackGroundImageView);
        Picasso.with(holder.itemView.getContext())
                .load(mMovie.getLarge_cover_image())
                .into(holder.mPosterImageView);
        holder.mSynopsisTextView.setText(mMovie.getSynopsis());
        holder.mTitleTextView.setText(mMovie.getTitle_long());
        //holder.mYearTextView.setText(String.valueOf(mMovie.getYear()));
        String genre = "";
        try {
            JSONArray mGA = new JSONArray(mMovie.getGenreJSONArrayString());
            for (int i = 0; i < mGA.length(); i++) {
                if (i == mGA.length() - 1) {
                    genre = genre + mGA.getString(i);
                } else {
                    genre = genre + mGA.getString(i) + " | ";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mLangTextView.setText(mMovie.getLanguage());
        holder.mRunTimeTextView.setText(String.valueOf(mMovie.getRuntime()));
        holder.mGenreTextView.setText(genre);
    }

    @Override
    public int getItemCount() {
        return mYTSMovieArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mBackGroundImageView;
        ImageView mPosterImageView;
        TextView mSynopsisTextView;
        TextView mTitleTextView;
        TextView mGenreTextView;
        TextView mYearTextView;
        TextView mRunTimeTextView;
        TextView mLangTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mBackGroundImageView = (ImageView) itemView.findViewById(R.id.backgroundImageView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.moviePosterImageView);
            mSynopsisTextView = (TextView) itemView.findViewById(R.id.synopsisTextView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.titleTV);
            mGenreTextView = (TextView) itemView.findViewById(R.id.genreTV);
            mYearTextView = (TextView) itemView.findViewById(R.id.releaseDateTV);
            mRunTimeTextView = (TextView) itemView.findViewById(R.id.runTimeTextView);
            mLangTextView = (TextView) itemView.findViewById(R.id.languageTextView);
        }
    }
}
