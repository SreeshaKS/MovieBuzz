package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreesha.android.moviebuzz.FireBaseUI.FireBaseRVAdapter;
import com.sreesha.android.moviebuzz.Networking.FBC;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.Utility;
import com.sreesha.android.moviebuzz.R;
import com.sreesha.android.moviebuzz.Settings.LoginActivity;

import java.util.Iterator;


public class CustomReviewsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Firebase mFireBaseRef;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference mCustomReviewsRef = mRootRef.child(FBC.REVIEWS);

    MovieDataInstance mMovieDataInstance;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    boolean mIsReviewAvailable = false;
    boolean mIsSignUpNeeded = false;
    String reviewKey = null;

    FireBaseRVAdapter<CRViewHolder, MovieReviewInstance>
            mRVFireBaseAdapter;

    public CustomReviewsFragment() {
        // Required empty public constructor
    }

    public static CustomReviewsFragment newInstance(MovieDataInstance mMovieDataInstance, String param2) {
        CustomReviewsFragment fragment = new CustomReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(MoviePhotosFragment.MOVIE_PARCELABLE_KEY, mMovieDataInstance);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieDataInstance = getArguments().getParcelable(MoviePhotosFragment.MOVIE_PARCELABLE_KEY);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFireBaseRef = new Firebase(FBC.FIREBASE_URL).child(FBC.REVIEWS);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("FireBaseAuth", "onAuthStateChanged:signed_in");
                } else {
                    mNetworkErrorMessageTextView.setText(
                            getActivity().getString(R.string.please_login_to_review)
                    );
                }
                // ...
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ReviewsData", "OnStart");
        mAuth.addAuthStateListener(mAuthListener);
        if (user != null) {

        } else {

        }
    }

    public class CRViewHolder extends RecyclerView.ViewHolder {
        public CardView expandedReviewContentCardView;
        public TextView authorTextView;
        public TextView contentOverViewTextView;
        public TextView reviewMovieNameTextView;
        public ImageView reviewShareImageView;

        public CRViewHolder(View itemView) {
            super(itemView);
            expandedReviewContentCardView = (CardView) itemView.findViewById(R.id.reviewExpandedContentCard);
            authorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
            contentOverViewTextView = (TextView) itemView.findViewById(R.id.reviewExpandedContentTextView);
            reviewMovieNameTextView = (TextView) itemView.findViewById(R.id.reviewMovieNameView);
            reviewShareImageView = (ImageView) itemView.findViewById(R.id.reviewShareImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ReviewsData", "OnResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_reviews, container, false);
        initializeViewElements(view);
        initializeListeners();
        if (user != null) {
            mIsSignUpNeeded = false;
            if (Utility.isConnectedToNetwork(getActivity())) {
                mRefreshImageView.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.ic_perm_scan_wifi_black_48dp)
                );
                mNetworkErrorMessageTextView.setText(
                        getActivity().getString(R.string.please_connect_to_a_working_internet_connection_string)
                );
                mConnectionErrorCard.setVisibility(View.GONE);
            }
            mCustomReviewsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean reviewsFound = false;
                    if (dataSnapshot.getValue() != null) {
                        mIsReviewAvailable = true;
                        Iterator<DataSnapshot> iterator
                                = dataSnapshot.getChildren().iterator();
                        for (; iterator.hasNext(); ) {
                            DataSnapshot snap = iterator.next();

                            MovieReviewInstance instance =
                                    MovieReviewInstance.getMovieReviewInstanceFromDataSnapShot(snap);
                            if (instance.getAuthor().equals(user.getDisplayName())
                                    && instance.getMovie_id() == mMovieDataInstance.getMovieID()) {
                                reviewKey = snap.getKey();
                                Log.d("ReviewsData", reviewKey);
                                reviewsFound = true;
                                authorTextView.setText(instance.getAuthor());
                                contentOverViewTextView.setText(instance.getReview_content());
                                mMovieNameTextView.setText(mMovieDataInstance.getTitle());
                                break;
                            }
                        }
                        if (reviewsFound) {
                            mIsReviewAvailable = true;
                            mConnectionErrorCard.setVisibility(View.GONE);
                            expandedReviewContentCardView.setVisibility(View.VISIBLE);
                        } else if (getActivity() != null) {
                            mIsReviewAvailable = false;
                            mInternetErrorImageView.setVisibility(View.GONE);
                            mRefreshImageView.setImageDrawable(
                                    getActivity()
                                            .getResources()
                                            .getDrawable(R.drawable.ic_mode_edit_white_48dp)
                            );
                            mConnectionErrorCard.setVisibility(View.VISIBLE);
                            mNetworkErrorMessageTextView.setText(R.string.no_reviews_found_string);
                            expandedReviewContentCardView.setVisibility(View.GONE);
                        }

                    } else {
                        mIsReviewAvailable = false;
                        Log.d("ReviewsData", "NOReviewAvailbale");
                        mInternetErrorImageView.setVisibility(View.GONE);
                        mRefreshImageView.setImageDrawable(
                                getActivity()
                                        .getResources()
                                        .getDrawable(R.drawable.ic_mode_edit_white_48dp)
                        );
                        mConnectionErrorCard.setVisibility(View.VISIBLE);
                        mNetworkErrorMessageTextView.setText(R.string.no_reviews_found_string);
                        expandedReviewContentCardView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("ReviewsData", databaseError.getMessage());
                    mIsReviewAvailable = false;
                }
            });
            setupFireBaseRecyclerAdapter();
        } else {
            mIsSignUpNeeded = true;
            mConnectionErrorCard.setVisibility(View.VISIBLE);
            expandedReviewContentCardView.setVisibility(View.GONE);
            mInternetErrorImageView.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_person_add_white_48dp)
            );
            mRefreshImageView.setVisibility(View.GONE);
            mNetworkErrorMessageTextView.setText(
                    getActivity().getString(R.string.please_login_to_review)
            );
        }

        return view;
    }

    void setupFireBaseRecyclerAdapter() {
        FirebaseRecyclerAdapter<MovieReviewInstance, CRViewHolder>
                adapter = new FirebaseRecyclerAdapter<MovieReviewInstance, CRViewHolder>(
                MovieReviewInstance.class
                , R.layout.single_custom_review_item
                , CRViewHolder.class
                , mCustomReviewsRef.orderByChild("review_id").equalTo(user.getUid())
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
                viewHolder.contentOverViewTextView.setText(model.getReview_content());
                viewHolder.authorTextView.setText(model.getAuthor());
                viewHolder.reviewMovieNameTextView.setText(model.getMovie_name());
            }
        };
        mOtherReviewsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mOtherReviewsRV.setAdapter(adapter);
    }

    CardView expandedReviewContentCardView;
    TextView authorTextView;
    TextView contentOverViewTextView;
    TextView reviewMovieNameTextView;
    ImageView reviewShareImageView;
    CardView mConnectionErrorCard;
    TextView mNetworkErrorMessageTextView;
    CardView mRefreshCard;
    ImageView mRefreshImageView;
    ImageView mInternetErrorImageView;
    TextView mMovieNameTextView;
    EditText mReviewEditEditText;
    TextInputLayout mReviewTextInputLayout;
    MovieReviewInstance userReviewInstance;
    RecyclerView mOtherReviewsRV;

    void initializeViewElements(View view) {

        expandedReviewContentCardView = (CardView) view.findViewById(R.id.reviewExpandedContentCard);
        expandedReviewContentCardView.setVisibility(View.GONE);
        authorTextView = (TextView) view.findViewById(R.id.authorTextView);
        contentOverViewTextView = (TextView) view.findViewById(R.id.reviewExpandedContentTextView);
        reviewShareImageView = (ImageView) view.findViewById(R.id.reviewShareImageView);

        mConnectionErrorCard = (CardView) view.findViewById(R.id.errorMessageDisplayCard);
        mNetworkErrorMessageTextView = (TextView) view.findViewById(R.id.networkErrorMessageTextView);
        mRefreshImageView = (ImageView) view.findViewById(R.id.refreshImageView);
        mInternetErrorImageView = (ImageView) view.findViewById(R.id.internetErrorImageView);
        mRefreshCard = (CardView) view.findViewById(R.id.refreshCard);
        mMovieNameTextView = (TextView) view.findViewById(R.id.reviewMovieNameView);
        mOtherReviewsRV = (RecyclerView) view.findViewById(R.id.otherReviewsRecyclerView);
    }

    void showReviewEditDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(mMovieDataInstance.getTitle())
                .customView(R.layout.edit_review_layout, true)
                .positiveText("Save")
                .negativeText("Delete/Discard")
                .neutralText("Back").build();
        dialog.getActionButton(DialogAction.NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsReviewAvailable) {
                    Log.d("ReviewData", mFireBaseRef.child(reviewKey).toString());
                    String data = mReviewEditEditText
                            .getText().toString();
                    userReviewInstance = new MovieReviewInstance(
                            mMovieDataInstance.getMovieID()
                            , -1
                            , user.getUid()
                            , user.getDisplayName()
                            , data
                            , user.getPhotoUrl().toString()
                            , -1
                    );
                    mFireBaseRef.child(reviewKey).setValue(userReviewInstance);
                    dialog.dismiss();
                } else {
                    Log.d("DialogErr", "Input Callback Called");
                    String input = mReviewEditEditText.getText().toString();
                    if (input != null && !input.isEmpty()) {
                        if (user != null) {
                            mConnectionErrorCard.setVisibility(View.GONE);
                            userReviewInstance =
                                    new MovieReviewInstance(
                                            mMovieDataInstance.getMovieID()
                                            , -1
                                            , user.getUid()
                                            , user.getDisplayName()
                                            , input.toString()
                                            , user.getPhotoUrl().toString()
                                            , -1
                                            , mMovieDataInstance.getTitle()
                                    );
                                      /*mFireBaseRef.child(String.valueOf(mMovieDataInstance.getMovieID()))
                                        .push().setValue(instance);*/
                            mFireBaseRef.push().setValue(userReviewInstance);
                            //TODO:Use FireBase to add Data
                        } else {
                            mNetworkErrorMessageTextView.setText(
                                    getActivity().getString(R.string.please_login_to_review)
                            );
                        }
                    } else {
                        mConnectionErrorCard.setVisibility(View.VISIBLE);
                        mNetworkErrorMessageTextView.setText(
                                getActivity().getString(R.string.no_reviews_found_string)
                        );
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Delete User Review
                if (mIsReviewAvailable) {
                    Log.d("ReviewData", mFireBaseRef.child(reviewKey).toString());
                    mIsReviewAvailable = false;
                    mFireBaseRef.child(reviewKey).removeValue();
                    reviewKey = null;
                }
                dialog.dismiss();
            }
        });
        mReviewTextInputLayout = (TextInputLayout) dialog.getCustomView().findViewById(R.id.reviewTextInputLayout);
        mReviewEditEditText = (EditText) dialog.getCustomView().findViewById(R.id.reviewEditEditText);
        if (mIsReviewAvailable) {
            mReviewTextInputLayout.setHint("Write Your Review Here");
            if (contentOverViewTextView.getText().toString() != null
                    || !contentOverViewTextView.getText().toString().isEmpty()) {
                mReviewEditEditText.setText(contentOverViewTextView.getText().toString());
            }
        } else {
            mReviewTextInputLayout.setHint("Edit Your Review");
        }
        dialog.show();
    }

    void initializeListeners() {
        expandedReviewContentCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showReviewEditDialog();
                return false;
            }
        });

        mRefreshCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsSignUpNeeded) {
                    getActivity().startActivity(new Intent(getActivity()
                            , LoginActivity.class));
                    getActivity().finish();
                } else {
                    showReviewEditDialog();
                }
            }
        });
    }
}
