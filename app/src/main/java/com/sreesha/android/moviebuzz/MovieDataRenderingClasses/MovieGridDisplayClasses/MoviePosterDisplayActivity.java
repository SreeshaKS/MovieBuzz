package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.R;

public class MoviePosterDisplayActivity extends AppCompatActivity {
    AppBarLayout appbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_poster_display);

        appbarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        Log.e("ExpandedPoserDebug","URL:"+getIntent().getStringExtra("mPosterURL"));
        final ImageView expandedPosterImageView = (ImageView) findViewById(R.id.expandedPosterImageView);
        Target expandedPosterTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("ExpandedPoserDebug","BitmapLoaded");
                expandedPosterImageView.setImageBitmap(bitmap);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                        int primary = getResources().getColor(R.color.colorPrimary);
                        appbarLayout.setBackgroundColor(palette.getMutedColor(primary));
                        appbarLayout.setBackgroundColor(palette.getDarkVibrantColor(primaryDark));
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("ExpandedPoserDebug","Error in loading");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        expandedPosterImageView.setTag(expandedPosterTarget);
        Picasso.with(getApplicationContext())
                .load(getIntent().getStringExtra("mPosterURL"))
                .into(expandedPosterTarget);

    }
}
