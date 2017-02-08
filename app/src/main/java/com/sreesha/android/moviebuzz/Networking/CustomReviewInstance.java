package com.sreesha.android.moviebuzz.Networking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sreesha on 30-07-2016.
 */
public class CustomReviewInstance extends MovieReviewInstance implements Parcelable{


    User mCurrentUser;
    public CustomReviewInstance() {
    }
    public CustomReviewInstance(long MOVIE_ID, String REVIEW_ID, String AUTHOR, String REVIEW_CONTENT) {
        super(MOVIE_ID, -1, REVIEW_ID, AUTHOR, REVIEW_CONTENT, null, -1);
    }

    protected CustomReviewInstance(Parcel in) {
        super(in);
        mCurrentUser = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mCurrentUser, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomReviewInstance> CREATOR = new Creator<CustomReviewInstance>() {
        @Override
        public CustomReviewInstance createFromParcel(Parcel in) {
            return new CustomReviewInstance(in);
        }

        @Override
        public CustomReviewInstance[] newArray(int size) {
            return new CustomReviewInstance[size];
        }
    };
    public User getCurrentUser() {
        return mCurrentUser;
    }
}
