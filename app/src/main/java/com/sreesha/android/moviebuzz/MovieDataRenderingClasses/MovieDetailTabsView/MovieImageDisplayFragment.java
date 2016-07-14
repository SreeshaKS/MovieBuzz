package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.MovieImage;
import com.sreesha.android.moviebuzz.R;

public class MovieImageDisplayFragment extends Fragment {
    MovieImage mMovieImage=null;
    public static final String MOVIE_IMAGE_PARCELABLE_KEY = "movieImageParcelableKey";
    public static final String MOVIE_IMAGE_URL_KEY = "movieImageURLKey";
    ImageView mBackDropPagerImageView;
    String imageURL=null;
    public MovieImageDisplayFragment() {
        // Required empty public constructor
    }

    public static MovieImageDisplayFragment newInstance(MovieImage mMovieImage) {
        MovieImageDisplayFragment fragment = new MovieImageDisplayFragment();
        Bundle args = new Bundle();
        Log.e("MovieBackDropPager","Creating a new INastance with image backdrop object");
        args.putParcelable(MOVIE_IMAGE_PARCELABLE_KEY, mMovieImage);
        fragment.setArguments(args);
        return fragment;
    }
    public static MovieImageDisplayFragment newInstance(String imageURL) {
        MovieImageDisplayFragment fragment = new MovieImageDisplayFragment();
        Bundle args = new Bundle();
        Log.e("MovieBackDropPager","Creating a new INstance with URL");
        args.putString(MOVIE_IMAGE_URL_KEY,imageURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MovieBackDropPager","onCreateCalled");
        if (savedInstanceState != null) {
            if( savedInstanceState.getParcelable(MOVIE_IMAGE_PARCELABLE_KEY)!=null){
                mMovieImage=savedInstanceState.getParcelable(MOVIE_IMAGE_PARCELABLE_KEY);
                Log.e("MovieBackDropPager","Restoring Parcelable");
            }else{
                imageURL = savedInstanceState.getString(MOVIE_IMAGE_URL_KEY);
                 Log.e("MovieBackDropPager","Restoring Image URL");
            }

        } else {
            if (getArguments() != null) {
                if(getArguments().getParcelable(MOVIE_IMAGE_PARCELABLE_KEY)!=null) {
                    mMovieImage = getArguments().getParcelable(MOVIE_IMAGE_PARCELABLE_KEY);
                    Log.e("MovieBackDropPager","Getting Image Object From Arguments");
                }else{
                    imageURL = getArguments().getString(MOVIE_IMAGE_URL_KEY);
                    Log.e("MovieBackDropPager","Getting Image URL Arguments");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_IMAGE_PARCELABLE_KEY,mMovieImage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_image_display, container, false);
        Log.e("MovieBackDropPager","onCreateViewCalled");
        mBackDropPagerImageView = (ImageView) view.findViewById(R.id.backDropPagerImageView);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MovieBackDropPager","onResume");
        Target mImageViewTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBackDropPagerImageView.setImageBitmap(bitmap);
                Log.e("MovieBackDropPager","Bitmap rendered");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("MovieBackDropPager","Bitmap Error");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        String URL;
        if(mMovieImage!=null) {
            URL = APIUrls.BASE_IMAGE_URL
                    + "/" + APIUrls.API_IMG_W_342
                    + mMovieImage.getImagePath();
            Log.e("MovieBackDropPager","BackDropIamge Object URL : "+URL);
        }else{
            URL=imageURL;
            Log.e("MovieBackDropPager","Image URL Loaded"+URL);
        }
        Picasso
                .with(getActivity())
                .load(URL)
                .into(mImageViewTarget);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
