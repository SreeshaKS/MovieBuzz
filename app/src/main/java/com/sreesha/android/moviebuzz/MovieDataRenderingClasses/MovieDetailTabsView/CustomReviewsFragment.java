package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieDetailTabsView;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreesha.android.moviebuzz.DataHandlerClasses.MovieContract;
import com.sreesha.android.moviebuzz.Networking.CustomReviewInstance;
import com.sreesha.android.moviebuzz.Networking.FBC;
import com.sreesha.android.moviebuzz.Networking.MovieDataInstance;
import com.sreesha.android.moviebuzz.Networking.MovieReviewInstance;
import com.sreesha.android.moviebuzz.Networking.Utility;
import com.sreesha.android.moviebuzz.R;
import com.sreesha.android.moviebuzz.Settings.LoginActivity;

import java.util.Iterator;
import java.util.Map;


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
                            if (instance.getAUTHOR().equals(user.getDisplayName())
                                    && instance.getMOVIE_ID() == mMovieDataInstance.getMovieID()) {
                                reviewsFound = true;
                                authorTextView.setText(instance.getAUTHOR());
                                contentOverViewTextView.setText(instance.getREVIEW_CONTENT());
                                mMovieNameTextView.setText(mMovieDataInstance.getTitle());
                                break;
                            }
                        }
                        if (reviewsFound) {
                            mIsReviewAvailable=true;
                            mConnectionErrorCard.setVisibility(View.GONE);
                            expandedReviewContentCardView.setVisibility(View.VISIBLE);
                        } else if (getActivity() != null) {
                            mIsReviewAvailable=false;
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
                        mIsReviewAvailable=false;
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
                if(mIsReviewAvailable){
                    //TODO:Edit User Review
                    dialog.dismiss();
                }else{
                    Log.d("DialogErr", "Input Callback Called");
                    String input = mReviewEditEditText.getText().toString();
                    if (input != null && !input.isEmpty()) {
                        if (user != null) {
                            mConnectionErrorCard.setVisibility(View.GONE);
                            MovieReviewInstance reviewInstance =
                                    new MovieReviewInstance(
                                            mMovieDataInstance.getMovieID()
                                            , -1
                                            , user.getUid()
                                            , user.getDisplayName()
                                            , input.toString()
                                            , user.getPhotoUrl().toString()
                                            , -1
                                    );
                                      /*mFireBaseRef.child(String.valueOf(mMovieDataInstance.getMovieID()))
                                        .push().setValue(instance);*/
                            mFireBaseRef.push().setValue(reviewInstance);
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
                dialog.dismiss();
            }
        });
        mReviewTextInputLayout = (TextInputLayout) dialog.getCustomView().findViewById(R.id.reviewTextInputLayout);
        mReviewEditEditText = (EditText) dialog.getCustomView().findViewById(R.id.reviewEditEditText);
        if(mIsReviewAvailable){
            mReviewTextInputLayout.setHint("Write Your Review Here");
            if(contentOverViewTextView.getText().toString()!=null
                    ||!contentOverViewTextView.getText().toString().isEmpty()){
                mReviewEditEditText.setText(contentOverViewTextView.getText().toString());
            }
        }else{
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
                showReviewEditDialog();
            }
        });
    }
}
