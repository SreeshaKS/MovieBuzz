package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Animation.CustomAnimator;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.R;

import java.util.ArrayList;

/**
 * Created by Sreesha on 17-05-2016.
 */
public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {
    ArrayList<MovieImage> backDropsList;
    ArrayList<MovieImage> posterList;
    Context mContext;
    private static final int POSTER_TYPE =1;
    private static final int BACKDROP_TYPE =1;

    public PhotosRecyclerViewAdapter(ArrayList<MovieImage> backDropsList
            , ArrayList<MovieImage> posterList) {
        this.backDropsList = backDropsList;
        this.posterList = posterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_movie_photo_item, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Target imageViewTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null) {
                    holder.posterImageView.setImageBitmap(bitmap);
                    Palette.PaletteAsyncListener asyncListener = new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            holder.imageContainerCardView
                                    .setCardBackgroundColor(
                                            palette
                                                    .getDarkMutedColor(
                                                            mContext
                                                                    .getResources()
                                                                    .getColor(R.color.colorPrimary)
                                                    )
                                    );
                            holder.imageBackgroundSheetCardView
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
        String URL;
        if(getItemViewType(position)==POSTER_TYPE){
            URL = APIUrls.BASE_IMAGE_URL
                    + "/" + APIUrls.API_IMG_W_500
                    + posterList.get(position).getImagePath();
        }else{
            URL = APIUrls.BASE_IMAGE_URL
                    + "/" + APIUrls.API_IMG_W_500
                    + posterList.get(position).getImagePath();
        }
        Picasso.with(mContext)
                .load(URL)
                //.resize(150,200)
                .placeholder(R.drawable.error_person_avathar)
                .error(R.drawable.error_person_avathar)
                .into(imageViewTarget);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return POSTER_TYPE;
        }else{
            return BACKDROP_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (backDropsList != null && posterList != null) {
            return (posterList.size());
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        CardView imageContainerCardView;
        CardView imageBackgroundSheetCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.photoImageView);
            imageContainerCardView = (CardView) itemView.findViewById(R.id.photoContainer);
            imageBackgroundSheetCardView = (CardView) itemView.findViewById(R.id.backGroundSheetCard);
        }
    }
    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        CustomAnimator.slideAnimRecyclerView(holder,true);
    }
}
