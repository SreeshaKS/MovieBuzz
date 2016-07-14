package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.PeopleDisplay;

/**
 * Created by Sreesha on 08-07-2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.PopularPeopleInstance;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

/**
 * Created by Sreesha on 28-01-2016.
 */
public class PopularPeopleRecyclerAdapter extends RecyclerView.Adapter<PopularPeopleRecyclerAdapter.ViewHolder> {
    String URL;
    Context mContext;
    private final int minimumOffset = 10;
    ArrayList<PopularPeopleInstance> popularPeopleList;


    PopularPeopleRecyclerAdapter() {
    }

    PopularPeopleRecyclerAdapter(ArrayList<PopularPeopleInstance> popularPeopleList, Context mContext) {
        this.mContext = mContext;
        this.popularPeopleList = popularPeopleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popular_people_single_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("MovieTypes", "onBindViewHolder");
        holder.personContainerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof PeopleDisplayActivity) {
                    if (popularPeopleList
                            .get(holder.getAdapterPosition()) == null) {
                        Log.d("NullDebug", "Data null ");
                    } else {
                        ((PeopleDisplayActivity) v.getContext())
                                .onPersonClicked(popularPeopleList
                                        .get(holder.getAdapterPosition()));
                        Log.d("NullDebug", "Data NOT null ");
                    }

                }
            }
        });
        holder.personNameTextView.setText(popularPeopleList.get(position).getNAME());
        Target imageViewTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null) {
                    holder.mPersonImageView.setImageBitmap(bitmap);
                    Palette.PaletteAsyncListener asyncListener = new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            holder.personContainerCard
                                    .setCardBackgroundColor(
                                            palette
                                                    .getDarkMutedColor(
                                                            mContext
                                                                    .getResources()
                                                                    .getColor(R.color.colorPrimary)
                                                    )
                                    );
                            holder.backGroundSheetCard
                                    .setCardBackgroundColor(
                                            palette
                                                    .getLightMutedColor(
                                                            mContext
                                                                    .getResources()
                                                                    .getColor(R.color.colorPrimary)
                                                    )
                                    );
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

        URL = APIUrls.BASE_IMAGE_URL
                + "/" + APIUrls.API_IMG_W_342
                + popularPeopleList.get(position)
                .getPROFILE_PATH();

        Picasso
                .with(holder.itemView.getContext())
                .load(URL)
                .into(imageViewTarget);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        CustomAnimator.playFadeAnimation(holder.itemView, true);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        CustomAnimator.playFadeAnimation(holder.itemView, false);
    }

    @Override
    public int getItemCount() {
        if (popularPeopleList != null)
            return popularPeopleList.size();
        else
            return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPersonImageView;
        public CardView backGroundSheetCard;
        public CardView personContainerCard;
        public TextView personNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPersonImageView = (ImageView) itemView.findViewById(R.id.castProfileImageView);
            personContainerCard = (CardView) itemView.findViewById(R.id.profileCardContainer);
            backGroundSheetCard = (CardView) itemView.findViewById(R.id.backGroundSheetCard);
            personNameTextView = (TextView) itemView.findViewById(R.id.personNameTextView);
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
}

