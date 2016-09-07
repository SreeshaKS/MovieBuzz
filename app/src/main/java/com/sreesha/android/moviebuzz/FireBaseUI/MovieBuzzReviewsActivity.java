package com.sreesha.android.moviebuzz.FireBaseUI;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.sreesha.android.moviebuzz.Networking.FBC;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.R;

public class MovieBuzzReviewsActivity extends AppCompatActivity {
    RecyclerView mMovieBuzzReviewsRecyclerView;
    FloatingActionButton fab;


    private Firebase mFireBaseRef;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference mCustomReviewsRef = mRootRef.child(FBC.REVIEWS);
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView mNoReviewFoundTextView;
    AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_buzz_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        toolbar.setTitle("Movie Buzz Reviews");
        setSupportActionBar(toolbar);


        mFireBaseRef = new Firebase(FBC.FIREBASE_URL).child(FBC.REVIEWS);

        initializeViewElements();
        addListeners();

    }

    void initializeViewElements() {
        mNoReviewFoundTextView = (TextView) findViewById(R.id.noReviewFoundTextView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mMovieBuzzReviewsRecyclerView = (RecyclerView) findViewById(R.id.movieBuzzReviewsRecyclerView);
    }

    AnimatorSet animSet;

    AnimatorSet getCloudDownloadAnimationSet() {
        ObjectAnimator mScaleX =
                ObjectAnimator.ofFloat(fab, "scaleX", 1f, 1.2f, 1.3f);
        mScaleX.setDuration(1000);
        ObjectAnimator mScaleY =
                ObjectAnimator.ofFloat(fab, "scaleY", 1f, 1.2f, 1.3f);
        mScaleY.setDuration(1000);
        mScaleX.setRepeatCount(ObjectAnimator.INFINITE);
        mScaleY.setRepeatCount(ObjectAnimator.INFINITE);
        mScaleX.setRepeatMode(ObjectAnimator.REVERSE);
        mScaleY.setRepeatMode(ObjectAnimator.REVERSE);
        animSet = new AnimatorSet();
        animSet.playTogether(mScaleX, mScaleY);
        return animSet;
    }

    void addListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCloudDownloadAnimationSet().start();
                adapter.notifyDataSetChanged();
            }
        });
    }

    FirebaseRecyclerAdapter<MovieReviewInstance, CRViewHolder>
            adapter;

    @Override
    protected void onStart() {
        super.onStart();
        getCloudDownloadAnimationSet().start();
        adapter = new FirebaseRecyclerAdapter<MovieReviewInstance, CRViewHolder>(
                MovieReviewInstance.class
                , R.layout.single_custom_review_item
                , CRViewHolder.class
                , mCustomReviewsRef.orderByChild("page").equalTo(-1)
        ) {
            @Override
            public CRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_custom_review_item, parent, false);
                return new CRViewHolder(view);
            }

            @Override
            protected void populateViewHolder(CRViewHolder viewHolder, MovieReviewInstance model, int position) {
                Log.d("ReviewData", "\t" + position);
                if (mNoReviewFoundTextView.getVisibility() == View.VISIBLE)
                    mNoReviewFoundTextView.setVisibility(View.GONE);
                if (animSet != null && animSet.isRunning()) {
                    animSet.cancel();
                    fab.setImageDrawable(
                            getResources()
                                    .getDrawable(R.drawable.ic_cloud_done_white_36dp)
                    );
                }
                viewHolder.contentOverViewTextView.setText(model.getReview_content());
                viewHolder.authorTextView.setText(model.getAuthor());
                viewHolder.reviewMovieNameTextView.setText(model.getMovie_name());
                loadImagesUsingPicasso(viewHolder, model.getReview_url());
            }

            public void loadImagesUsingPicasso(final CRViewHolder holder, String URL) {
                Target posterImageTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.avatharImageView.setImageBitmap(bitmap);
                        Palette.PaletteAsyncListener asyncListener = new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                holder.mProfileImageBackgroundCV
                                        .setCardBackgroundColor(
                                                palette
                                                        .getVibrantColor(
                                                                getResources()
                                                                        .getColor(R.color.colorPrimary)
                                                        )
                                        );

                                holder.mAuthorDisplayCard
                                        .setCardBackgroundColor(
                                                palette
                                                        .getDarkVibrantColor(
                                                                getResources()
                                                                        .getColor(R.color.colorPrimary)
                                                        )
                                        );
                            }
                        };
                        Palette.from(bitmap).generate(asyncListener);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                if (holder.avatharImageView.getTag() != null) {
                    holder.avatharImageView.setTag(posterImageTarget);
                }
                Picasso
                        .with(getBaseContext())
                        .load(URL)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.ic_movie_white_48dp)
                        .error(R.drawable.ic_error_white_48dp)
                        .into(posterImageTarget);
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mNoReviewFoundTextView.getVisibility() == View.VISIBLE)
                    mNoReviewFoundTextView.setVisibility(View.GONE);
                if (adapter.getItemCount() != 0) {
                    fab.setImageDrawable(
                            getResources()
                                    .getDrawable(R.drawable.ic_cloud_done_white_36dp)
                    );
                    if (animSet != null && animSet.isRunning()) {
                        animSet.cancel();
                    }
                } else {

                }

            }
        });
        mMovieBuzzReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mMovieBuzzReviewsRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            mNoReviewFoundTextView.setVisibility(View.VISIBLE);
        } else {
            mNoReviewFoundTextView.setVisibility(View.GONE);
        }
    }

    public class CRViewHolder extends RecyclerView.ViewHolder {
        public CardView expandedReviewContentCardView;
        public TextView authorTextView;
        public TextView contentOverViewTextView;
        public TextView reviewMovieNameTextView;
        public ImageView reviewShareImageView;
        public ImageView avatharImageView;
        CardView mProfileImageBackgroundCV;
        CardView mAuthorDisplayCard;

        public CRViewHolder(View itemView) {
            super(itemView);
            expandedReviewContentCardView = (CardView) itemView.findViewById(R.id.reviewExpandedContentCard);
            authorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
            contentOverViewTextView = (TextView) itemView.findViewById(R.id.reviewExpandedContentTextView);
            reviewMovieNameTextView = (TextView) itemView.findViewById(R.id.reviewMovieNameView);
            reviewShareImageView = (ImageView) itemView.findViewById(R.id.reviewShareImageView);
            avatharImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
            mProfileImageBackgroundCV = (CardView) itemView.findViewById(R.id.profileImageBackgroundCV);
            mAuthorDisplayCard = (CardView) itemView.findViewById(R.id.authorDisplayCard);
        }
    }
}
