package com.sreesha.android.moviebuzz.Settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses.MoviePosterGridActivity;
import com.sreesha.android.moviebuzz.Networking.APIUrls;
import com.sreesha.android.moviebuzz.Networking.FBC;
import com.sreesha.android.moviebuzz.Networking.User;
import com.sreesha.android.moviebuzz.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnClickListener {


    GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    CardView mForwardProceedCardView;
    CardView mIconLoaderCardView;

    TextView mSkipLoginTextView;

    ProgressBar mProgressBar;
    private DatabaseReference mDatabase;

    AnimatorSet mIconLoaderAnimatorSet;
    ImageView mNextStepsImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyApplication", "onCreate-LoginActivity");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mFireBaseRef = new Firebase(FBC.FIREBASE_URL).child(FBC.USER);
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MoviePosterGridActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);
            proceedWithLoginActivity();
        }
    }

    private Firebase mFireBaseRef;

    private void writeNewUser(String userId, String name, String email
            , String photoURL) {
        User user = new User(name, email, userId, photoURL);
        mFireBaseRef.push().setValue(user);
    }

    void proceedWithLoginActivity() {
        mNextStepsImageView = (ImageView) findViewById(R.id.nextStepsImageView);
        mIconLoaderCardView = (CardView) findViewById(R.id.iconLoaderCardView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSkipLoginTextView = (TextView) findViewById(R.id.skipLoginTextView);
        mForwardProceedCardView = (CardView) findViewById(R.id.forwardProceedCardView);

        mSkipLoginTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MoviePosterGridActivity.class));
                finish();
            }
        });
        mForwardProceedCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mNextStepsImageView.setVisibility(View.GONE);
                ObjectAnimator mScaleXAnim
                        = ObjectAnimator.ofFloat(mForwardProceedCardView
                        , "scaleX", 1, 30);

                ObjectAnimator mScaleYAnim
                        = ObjectAnimator.ofFloat(mForwardProceedCardView
                        , "scaleY", 1, 30);

                AnimatorSet set = new AnimatorSet();
                set.setDuration(400);
                set.playTogether(mScaleXAnim, mScaleYAnim);

                Animator.AnimatorListener
                        mListener
                        = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        startActivity(new Intent(LoginActivity.this, MoviePosterGridActivity.class));
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                };
                set.addListener(mListener);
                set.start();
            }
        });
        mForwardProceedCardView.setEnabled(false);
        mForwardProceedCardView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    stopIconLoaderAnimation();
                    /*Log.d("FireBaseAuth", "User Signed In");
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    if (user.getPhotoUrl() != null) {
                        writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail()
                                , user.getPhotoUrl().toString());
                    } else {
                        writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail()
                                , null);
                    }*/
                    mForwardProceedCardView.setEnabled(true);
                    mForwardProceedCardView.setVisibility(View.VISIBLE);
                } else {
                    // User is signed out
                    Log.d("FireBaseAuth", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(APIUrls.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        startIconLoaderAnimation();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void startIconLoaderAnimation() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setEnabled(true);
        mProgressBar.setIndeterminate(true);

        Log.d("AnimatorDebug", "Starting Animation");
        ObjectAnimator mScaleAnimatorX = ObjectAnimator.ofFloat(mIconLoaderCardView, "scaleX", 1, 1.2f);
        ObjectAnimator mScaleAnimatorY = ObjectAnimator.ofFloat(mIconLoaderCardView, "scaleY", 1, 1.2f);

        mScaleAnimatorX.setRepeatMode(ValueAnimator.REVERSE);
        mScaleAnimatorY.setRepeatCount(ValueAnimator.INFINITE);

        mScaleAnimatorX.setRepeatCount(ValueAnimator.INFINITE);
        mScaleAnimatorY.setRepeatMode(ValueAnimator.REVERSE);

        mIconLoaderAnimatorSet = new AnimatorSet();
        mIconLoaderAnimatorSet.setDuration(1000);
        mIconLoaderAnimatorSet.playTogether(mScaleAnimatorX, mScaleAnimatorY);
        mIconLoaderAnimatorSet.start();
    }

    void stopIconLoaderAnimation() {
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setEnabled(false);

        Log.d("AnimatorDebug", "Stopping Animation");
        if (mIconLoaderAnimatorSet != null) {
            if (mIconLoaderAnimatorSet.isRunning()) {
                mIconLoaderAnimatorSet.cancel();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);
            } else {
                Log.d("FireBaseAuth", "Google Sign In Failed" + result.getStatus());
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("CompleteList", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("CompleteList", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("CompleteList", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}

