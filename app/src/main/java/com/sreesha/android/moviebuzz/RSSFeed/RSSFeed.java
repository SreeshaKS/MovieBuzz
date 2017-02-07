package com.sreesha.android.moviebuzz.RSSFeed;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sreesha.android.moviebuzz.Networking.DownloadMovieSpecifics;
import com.sreesha.android.moviebuzz.Networking.YTSAsyncResult;
import com.sreesha.android.moviebuzz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static com.sreesha.android.moviebuzz.RSSFeed.MovieTorrentFragment.MOVIE_TORRENT_FRAGMENT_TAG;

public class RSSFeed extends AppCompatActivity {
    DownloadMovieSpecifics mMovieSpecificsAsyncTask;
    CardView fab;

    CardView ratingCard;
    TextView mRatingTextView;
    CardView resolutionCard;
    TextView mResolutionTextView;
    CardView genreCard;
    TextView mGenreTextView;
    CardView sortByCard;
    TextView mSortByTextView;
    EditText mSearchQueryEditText;

    CardView mExpandedOptionsCard;

    CoordinatorLayout mRssFeedCoordinatorLayout;
    CardView mFilterDisplayCardView;
    ImageView mFilterImageView;
    ImageView mCloseIconImageView;
    CardView mSearchCard;
    FrameLayout mFilterFrameLayout;

    boolean isRatingCardSelected = false;
    boolean isResolutionCardSelected = false;
    boolean isGenreCardSelected = false;
    boolean isSortByCardSelected = false;

    boolean isFilterVisible = false;

    RecyclerView mOptionsRecyclerView;
    OptionsRVAdapter mOptionsRVAdapter;
    GridLayoutManager mGridLayoutManager;
    String[] mRatingArray = {
            "All",
            "1+",
            "2+",
            "3+",
            "4+",
            "5+",
            "6+",
            "7+",
            "8+",
            "9+",
    };
    String[] mResolutionArray = {
            "All",
            "720p",
            "1080p",
            "3D",
    };
    String[] mSortByArray = {
            "Title",
            "Year",
            "Peers",
            "Seeds",
            "Download_Count"
            , "Like_Count"
            , "Date_Added"
    };
    String[] genreArray = {
            "All",
            "Action",
            "Comedy",
            "War",
            "Crime",
            "Drama",
            "Music",

            "Documentary",
            "Adventure",
            "Animation",
            "Film-Noir",
            "Biography",
            "Thriller",
            "Romance",
            "History",
            "Musical",
            "Fantasy",
            "Mystery",
            "Western",

            "Family",
            "Horror",
            "Sport",
            "Sci-Fi",
    };
    String ratingQueryParam = "All";
    String resolutionQueryParam = "All";
    String genreQueryParam = "All";
    String sortByQueryParam = "All";
    String titleQueryParam = "null";

    View.OnClickListener fCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("clickDebug", "Clicked");
            displayFilterDialogue();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssfeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (CardView) findViewById(R.id.fab);
        mRssFeedCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.rssFeedCoordinatorLayout);
        mFilterDisplayCardView = (CardView) findViewById(R.id.filterDisplayCardView);
        mFilterFrameLayout = (FrameLayout) findViewById(R.id.filterFrameLayout);
        mFilterFrameLayout.setEnabled(false);
        mFilterDisplayCardView.setVisibility(View.GONE);

        mFilterFrameLayout.requestDisallowInterceptTouchEvent(false);

        mSearchQueryEditText = (EditText) findViewById(R.id.searchQueryEditText);

        ratingCard = (CardView) findViewById(R.id.ratingFilterCard);
        mRatingTextView = (TextView) findViewById(R.id.ratingTextView);
        resolutionCard = (CardView) findViewById(R.id.resolutionFilterCard);
        mResolutionTextView = (TextView) findViewById(R.id.resolutionTextView);
        genreCard = (CardView) findViewById(R.id.genreFilterCard);
        mSearchCard = (CardView) findViewById(R.id.searchCard);

        mGenreTextView = (TextView) findViewById(R.id.genreTextView);
        sortByCard = (CardView) findViewById(R.id.orderByFilterCard);
        mSortByTextView = (TextView) findViewById(R.id.orderByTextView);
        mExpandedOptionsCard = (CardView) findViewById(R.id.expandedOptionsCard);

        mOptionsRecyclerView = (RecyclerView) findViewById(R.id.optionsRecyclerView);
        mOptionsRVAdapter = new OptionsRVAdapter();
        mGridLayoutManager = new GridLayoutManager(getBaseContext(), 5);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                /*if (position < 5) {
                    return 4;
                } else if (position > 5 && position <= 17) {
                    return 2;
                } else if (position > 17) {
                    return 5;
                }*/
                return 3;
            }
        });
        mOptionsRecyclerView.setLayoutManager(mGridLayoutManager);
        mOptionsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getBaseContext()
                        , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isRatingCardSelected) {
                            isRatingCardSelected = false;
                            ratingQueryParam = mRatingArray[position];
                            playReverseExpandedOptionsAnimation(ratingCard
                                    , mRatingTextView
                                    , ratingQueryParam);

                        } else if (isResolutionCardSelected) {
                            isResolutionCardSelected = false;
                            resolutionQueryParam = mResolutionArray[position];
                            playReverseExpandedOptionsAnimation(resolutionCard
                                    , mResolutionTextView
                                    , resolutionQueryParam);
                        } else if (isGenreCardSelected) {
                            isGenreCardSelected = false;
                            genreQueryParam = genreArray[position];
                            playReverseExpandedOptionsAnimation(genreCard
                                    , mGenreTextView
                                    , genreQueryParam);
                        } else if (isSortByCardSelected) {
                            isSortByCardSelected = false;
                            sortByQueryParam = mSortByArray[position];
                            playReverseExpandedOptionsAnimation(sortByCard
                                    , mSortByTextView
                                    , sortByQueryParam);
                        }
                    }
                }));
        mOptionsRecyclerView.setAdapter(mOptionsRVAdapter);

        mFilterImageView = (ImageView) findViewById(R.id.filterImageView);
        mCloseIconImageView = (ImageView) findViewById(R.id.closeIconImageView);
        mCloseIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseFilterAnimation();
            }
        });
        mSearchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseFilterAnimation();
                gatherFilterDataAndStartQuery();
            }
        });
        fab.setOnClickListener(fCL);
        ratingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRatingCardSelected = true;
                mOptionsRVAdapter.notifyDataSetChanged();
                mRatingTextView.setVisibility(View.GONE);
                ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(ratingCard, "scaleX", 1, 20);
                ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(ratingCard, "scaleY", 1, 20);
                AnimatorSet set = new AnimatorSet();
                Animator.AnimatorListener mL = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ratingCard.setVisibility(View.GONE);
                        mExpandedOptionsCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                set.addListener(mL);
                set.playTogether(mScaleXAnimator, mScaleYAnimator);
                set.start();
            }
        });
        resolutionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionsRVAdapter.notifyDataSetChanged();
                isResolutionCardSelected = true;
                mResolutionTextView.setVisibility(View.GONE);
                ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(resolutionCard, "scaleX", 1, 20);
                ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(resolutionCard, "scaleY", 1, 20);
                AnimatorSet set = new AnimatorSet();
                Animator.AnimatorListener mL = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resolutionCard.setVisibility(View.GONE);
                        mExpandedOptionsCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                set.addListener(mL);
                set.playTogether(mScaleXAnimator, mScaleYAnimator);
                set.start();
            }
        });
        genreCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionsRVAdapter.notifyDataSetChanged();
                isGenreCardSelected = true;
                mGenreTextView.setVisibility(View.GONE);
                ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(genreCard, "scaleX", 1, 20);
                ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(genreCard, "scaleY", 1, 20);
                AnimatorSet set = new AnimatorSet();
                Animator.AnimatorListener mL = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        genreCard.setVisibility(View.GONE);
                        mExpandedOptionsCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                set.addListener(mL);
                set.playTogether(mScaleXAnimator, mScaleYAnimator);
                set.start();
            }
        });
        sortByCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionsRVAdapter.notifyDataSetChanged();
                isSortByCardSelected = true;
                mSortByTextView.setVisibility(View.GONE);
                ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(sortByCard, "scaleX", 1, 20);
                ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(sortByCard, "scaleY", 1, 20);
                AnimatorSet set = new AnimatorSet();
                Animator.AnimatorListener mL = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        sortByCard.setVisibility(View.GONE);
                        mExpandedOptionsCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                set.addListener(mL);
                set.playTogether(mScaleXAnimator, mScaleYAnimator);
                set.start();
            }
        });
        if (savedInstanceState == null)
            downloadYTSMovieData();
    }

    private void playReverseExpandedOptionsAnimation(CardView cardView, final TextView textView, final String displayText) {
        mExpandedOptionsCard.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);

        ObjectAnimator mScaleXAnimator = ObjectAnimator.ofFloat(cardView, "scaleX", 20, 1);
        ObjectAnimator mScaleYAnimator = ObjectAnimator.ofFloat(cardView, "scaleY", 20, 1);
        AnimatorSet set = new AnimatorSet();
        Animator.AnimatorListener l = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(displayText);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        set.addListener(l);
        set.playTogether(mScaleXAnimator, mScaleYAnimator);
        set.start();
    }

    private void reverseFilterAnimation() {
        isFilterVisible = false;
        ObjectAnimator mFabYAnimator = ObjectAnimator.ofFloat(fab, "translationY"
                , -(mRssFeedCoordinatorLayout.getHeight() * 20 / 100) / 2, 0);
        ObjectAnimator mFabXAnimator = ObjectAnimator.ofFloat(fab, "translationX"
                , -mRssFeedCoordinatorLayout.getWidth() / 2.8f, 0);
        Log.d("Width", String.valueOf(mRssFeedCoordinatorLayout.getWidth()));
        mFabXAnimator.setInterpolator(new DecelerateInterpolator(2f));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.playTogether(mFabYAnimator, mFabXAnimator);

        fab.setPivotX(fab.getHeight() / 2);
        fab.setPivotY(fab.getWidth() / 2);

        ObjectAnimator circularRevealXScale = ObjectAnimator.ofFloat(fab
                , "scaleX"
                , 20
                , 1);
        ObjectAnimator circularRevealYScale = ObjectAnimator.ofFloat(fab
                , "scaleY"
                , 20
                , 1);
        fab.setVisibility(View.VISIBLE);
        mFilterDisplayCardView.setVisibility(View.GONE);


        AnimatorSet circularRSet = new AnimatorSet();
        circularRSet.setDuration(800);
        circularRSet.playTogether(circularRevealXScale, circularRevealYScale);
        Animator.AnimatorListener aL
                = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFilterFrameLayout.invalidate();
                mFilterFrameLayout.setEnabled(false);
                mFilterDisplayCardView.setEnabled(false);
                fab.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(fCL);
                mFilterImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.addListener(aL);
        finalSet.playSequentially(circularRSet, set);
        /*finalSet.playTogether(mFabYAnimator, mFabXAnimator);*/
        finalSet.start();
    }

    private void gatherFilterDataAndStartQuery() {
        titleQueryParam = mSearchQueryEditText.getText().toString();
        Log.d("QueryEditText", "Edit Test Value : " + titleQueryParam);
        //Update URL Object
        if (titleQueryParam == null || titleQueryParam.equals("") || titleQueryParam.isEmpty()) {
            URL = YTSAPI.buildMovieListBaseURI()
                    .appendQueryParameter(YTSAPI.PARAM_RATING, ratingQueryParam)
                    .appendQueryParameter(YTSAPI.PARAM_QUALITY, resolutionQueryParam)
                    .appendQueryParameter(YTSAPI.PARAM_GENRE, genreQueryParam)
                    .appendQueryParameter(YTSAPI.PARAM_SORT, sortByQueryParam)
                    .build().toString();

        } else {
            URL = YTSAPI.buildMovieListBaseURI()
                    .appendQueryParameter(YTSAPI.PARAM_QUERY_TERM, titleQueryParam)
                    .build().toString();
        }
        downloadYTSMovieData();
        Log.d("Query URL", URL);
    }

    private void displayFilterDialogue() {
        isFilterVisible = true;
        mFilterFrameLayout.setEnabled(true);
        Log.d("clickDebug", "Clicked");
        mFilterImageView.setVisibility(View.GONE);
        mFilterDisplayCardView.setEnabled(true);

        ObjectAnimator mFabYAnimator = ObjectAnimator.ofFloat(fab, "translationY"
                , 0, -(mRssFeedCoordinatorLayout.getHeight() * 20 / 100) / 2);
        ObjectAnimator mFabXAnimator = ObjectAnimator.ofFloat(fab, "translationX"
                , 0, -mRssFeedCoordinatorLayout.getWidth() / 2.8f);
        Log.d("Width", String.valueOf(mRssFeedCoordinatorLayout.getWidth()));
        mFabXAnimator.setInterpolator(new DecelerateInterpolator(2f));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.playTogether(mFabYAnimator, mFabXAnimator);
        fab.setPivotX(fab.getHeight() / 2);
        fab.setPivotY(fab.getWidth() / 2);

        ObjectAnimator circularRevealXScale = ObjectAnimator.ofFloat(fab
                , "scaleX"
                , 1
                , 20);
        ObjectAnimator circularRevealYScale = ObjectAnimator.ofFloat(fab
                , "scaleY"
                , 1
                , 20);
        AnimatorSet circularRSet = new AnimatorSet();
        circularRSet.setDuration(500);
        circularRSet.playTogether(circularRevealXScale, circularRevealYScale);
        Animator.AnimatorListener aL
                = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("anim", "Animation Ended");
                fab.setVisibility(View.GONE);
                fab.setEnabled(false);
                mFilterDisplayCardView.setVisibility(View.VISIBLE);
                //mFilterFrameLayout.invalidate();
                ObjectAnimator mCloseIconAnimator = ObjectAnimator.ofFloat(mCloseIconImageView
                        , "rotation", 0, 180);
                mCloseIconAnimator.setDuration(800);
                mCloseIconAnimator.setInterpolator(new DecelerateInterpolator(1));

                ObjectAnimator[] animatorScaleX
                        = {
                        ObjectAnimator.ofFloat(ratingCard, "scaleX", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(resolutionCard, "scaleX", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(genreCard, "scaleX", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(sortByCard, "scaleX", 0.8f, 1.2f, 1)
                };
                ObjectAnimator[] animatorScaleY
                        = {
                        ObjectAnimator.ofFloat(ratingCard, "scaleY", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(resolutionCard, "scaleY", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(genreCard, "scaleY", 0.8f, 1.2f, 1)
                        , ObjectAnimator.ofFloat(sortByCard, "scaleY", 0.8f, 1.2f, 1)
                };
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        animatorScaleX[0]
                        , animatorScaleX[1]
                        , animatorScaleX[2]
                        , animatorScaleX[3]
                        , animatorScaleY[0]
                        , animatorScaleY[1]
                        , animatorScaleY[2]
                        , animatorScaleY[3]
                );
                set.setDuration(600);
                set.setInterpolator(new FastOutSlowInInterpolator());


                AnimatorSet finalSet = new AnimatorSet();
                finalSet.playSequentially(set, mCloseIconAnimator);
                finalSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        circularRSet.addListener(aL);
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set, circularRSet);
        /*finalSet.playTogether(mFabYAnimator, mFabXAnimator);*/
        finalSet.start();
    }

    private class OptionsRVAdapter extends RecyclerView.Adapter<OptionsRVAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.yts_filter_options_recycler_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Random rand = new Random();
            int n = rand.nextInt(100);
            if (n > 0 && position % n == 0) {
                holder.mOptionsTextView.setTextSize(20);
            }
            if (isRatingCardSelected) {
                holder.mOptionsTextView.setText(mRatingArray[position]);
            } else if (isResolutionCardSelected) {
                holder.mOptionsTextView.setText(mResolutionArray[position]);
            } else if (isGenreCardSelected) {
                holder.mOptionsTextView.setText(genreArray[position]);
            } else if (isSortByCardSelected) {
                holder.mOptionsTextView.setText(mSortByArray[position]);
            }
        }

        @Override
        public int getItemCount() {
            if (isRatingCardSelected) {
                return mRatingArray.length;
            } else if (isResolutionCardSelected) {
                return mResolutionArray.length;
            } else if (isGenreCardSelected) {
                return genreArray.length;
            } else if (isSortByCardSelected) {
                return mSortByArray.length;
            } else {
                return 2;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView mOptionsCardView;
            TextView mOptionsTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mOptionsCardView = (CardView) itemView.findViewById(R.id.optionsCardView);
                mOptionsTextView = (TextView) itemView.findViewById(R.id.optionTextView);
            }
        }
    }

    ArrayList<YTSMovie> mYTSMovieArrayList;
    String URL = YTSAPI.buildMovieListBaseURI().build().toString();

    void downloadYTSMovieData() {
        mMovieSpecificsAsyncTask
                = new DownloadMovieSpecifics(getBaseContext()
                , new YTSAsyncResult() {
            @Override
            public void onMovieDataParsed(ArrayList<YTSMovie> YTSMovieArrayList) {
                if (YTSMovieArrayList != null) {
                    if (!YTSMovieArrayList.isEmpty()) {
                        Log.d("YTSArrayList", "Array Size\t" + YTSMovieArrayList.size());
                        RSSFeed.this.mYTSMovieArrayList = YTSMovieArrayList;
                        if (mMovieTorrentFragment != null && getSupportFragmentManager()
                                .findFragmentByTag(MOVIE_TORRENT_FRAGMENT_TAG) != null) {
                            mMovieTorrentFragment.onMovieDataChanged(RSSFeed.this.mYTSMovieArrayList);
                        } else {
                            startFragmentTransaction(YTSMovieArrayList);
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "No Torrents Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onResultJSON(JSONObject object) throws JSONException {
            }

            @Override
            public void onResultString(String stringObject, String errorString, String parseStatus) {

            }
        });
        mMovieSpecificsAsyncTask.execute(
                URL
        );
    }

    MovieTorrentFragment mMovieTorrentFragment;

    void startFragmentTransaction(ArrayList<YTSMovie> YTSMovieArrayList) {
        mMovieTorrentFragment = MovieTorrentFragment.newInstance(YTSMovieArrayList);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.movieTorrentDisplayContainer, mMovieTorrentFragment, MOVIE_TORRENT_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (isFilterVisible) {
            reverseFilterAnimation();
        } else {
            super.onBackPressed();
        }
    }
}
